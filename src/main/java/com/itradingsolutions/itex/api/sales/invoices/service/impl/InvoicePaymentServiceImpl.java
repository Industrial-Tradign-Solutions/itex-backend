package com.itradingsolutions.itex.api.sales.invoices.service.impl;

import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.sales.invoices.exceptions.InvoicePaymentException;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoicePaymentDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceHistoryAction;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceStatus;
import com.itradingsolutions.itex.api.sales.invoices.models.mapper.InvoicePaymentMapper;
import com.itradingsolutions.itex.api.sales.invoices.models.request.RegisterInvoicePaymentRequest;
import com.itradingsolutions.itex.api.sales.invoices.models.request.VoidInvoicePaymentRequest;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoicePaymentRepository;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoiceRepository;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceHistoryService;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoicePaymentService;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceAccessGuard;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceBalanceCalculator;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceFinder;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceMutationGuard;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceReceiptStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoicePaymentServiceImpl extends UtilServiceAbs implements IInvoicePaymentService {

    private final IInvoiceRepository repository;
    private final IInvoicePaymentRepository paymentRepository;
    private final InvoicePaymentMapper mapper;
    private final InvoiceAccessGuard accessGuard;
    private final InvoiceMutationGuard mutationGuard;
    private final InvoiceBalanceCalculator balanceCalculator;
    private final InvoiceReceiptStorage receiptStorage;
    private final InvoiceFinder finder;
    private final IInvoiceHistoryService historyService;
    private final IUserService userService;

    /**
     * Nothing is collectable on a draft and nothing is left to collect on a settled or cancelled
     * invoice, so only {@code ISSUED}/{@code PARTIAL_PAID} accept a payment. The receipt is
     * mandatory: a movement of money without proof is not registrable (invoicing guide §7).
     *
     * <p>The file is written to disk last, once every rule has passed, so a rejected payment leaves
     * nothing behind. A rollback after that point (history, recalculation) still orphans the file —
     * an infrastructure leftover, not a data inconsistency, and cheaper to live with than a
     * compensating transaction.</p>
     */
    @Override
    @Transactional
    public InvoicePaymentDTO register(UUID invoiceId, RegisterInvoicePaymentRequest request, MultipartFile receipt) {
        var invoice = loadPayable(invoiceId);

        if (receipt == null || receipt.isEmpty() || receipt.getOriginalFilename() == null)
            throw new InvoicePaymentException(simpleMessage("sales.invoice.payment.receipt-required"));

        assertAmountFits(invoice, request.amount());

        var payment = mapper.requestToEntity(request);
        payment.setInvoice(invoice);
        payment.setVoided(false);
        payment.setRegisteredBy(userService.getUserAuthenticated());
        payment.setCreatedAt(ZonedDateTime.now(zoneId));
        payment.setReceiptOriginalName(receipt.getOriginalFilename());
        payment.setReceiptPath(receiptStorage.store(invoice, receipt));

        var dto = mapper.entityToDTO(paymentRepository.save(payment));
        recalculate(invoice);
        historyService.addHistoryPayment(InvoiceHistoryAction.REGISTER_PAYMENT, null, dto, invoiceId);
        return dto;
    }

    /**
     * A registered payment is immutable: a mistake is voided with a reason and re-registered, so
     * the audit trail keeps both rows. The receipt file stays on disk for the same reason.
     */
    @Override
    @Transactional
    public InvoicePaymentDTO voidPayment(UUID invoiceId, UUID paymentId, VoidInvoicePaymentRequest request) {
        var invoice = finder.findByIdForUpdate(invoiceId);
        accessGuard.assertCanAccess(invoice);
        accessGuard.assertCanMutate(invoice);
        mutationGuard.assertLockedByCurrentUser(invoice);

        var payment = paymentRepository.findByIdAndInvoice_Id(paymentId, invoiceId)
                .orElseThrow(() -> new InvoicePaymentException(simpleMessage("sales.invoice.payment.not-exist")));

        if (payment.isVoided())
            throw new InvoicePaymentException(simpleMessage("sales.invoice.payment.already-voided"));

        var oldDto = mapper.entityToDTO(payment);

        payment.setVoided(true);
        payment.setVoidedReason(request.voidedReason());
        payment.setVoidedAt(ZonedDateTime.now(zoneId));
        payment.setVoidedBy(userService.getUserAuthenticated());

        var newDto = mapper.entityToDTO(paymentRepository.save(payment));
        recalculate(invoice);
        historyService.addHistoryPayment(InvoiceHistoryAction.VOID_PAYMENT, oldDto, newDto, invoiceId);
        return newDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoicePaymentDTO> listByInvoice(UUID invoiceId) {
        var invoice = finder.findById(invoiceId);
        accessGuard.assertCanAccess(invoice);
        return paymentRepository.fetchByInvoiceId(invoiceId).stream().map(mapper::entityToDTO).toList();
    }

    /**
     * Loads with a row-level lock held until the transaction ends, so the balance check and the
     * recalculation that follows it see a consistent picture: two simultaneous payments on the same
     * invoice can no longer both pass the "amount must fit in the outstanding balance" rule.
     */
    private InvoiceEntity loadPayable(UUID invoiceId) {
        var invoice = finder.findByIdForUpdate(invoiceId);
        accessGuard.assertCanAccess(invoice);
        accessGuard.assertCanMutate(invoice);
        mutationGuard.assertLockedByCurrentUser(invoice);

        if (!InvoiceStatus.COLLECTABLE.contains(invoice.getStatus()))
            throw new InvoicePaymentException(simpleMessage("sales.invoice.payment.not-payable"));

        return invoice;
    }

    /**
     * No overpayments: the guide leaves advances against future invoices out of v1, and a payment
     * beyond the balance would only produce a negative balance nobody can act on.
     */
    private void assertAmountFits(InvoiceEntity invoice, BigDecimal amount) {
        if (amount.compareTo(balanceCalculator.balance(invoice)) > 0)
            throw new InvoicePaymentException(simpleMessage("sales.invoice.payment.invalid-amount"));
    }

    private void recalculate(InvoiceEntity invoice) {
        balanceCalculator.apply(invoice);
        repository.save(invoice);
    }
}
