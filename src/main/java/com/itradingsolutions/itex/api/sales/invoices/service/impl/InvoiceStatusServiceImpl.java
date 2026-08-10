package com.itradingsolutions.itex.api.sales.invoices.service.impl;

import com.itradingsolutions.itex.api.common.salesconsecutive.models.enums.SalesConsecutiveType;
import com.itradingsolutions.itex.api.common.salesconsecutive.services.ISalesConsecutiveService;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceHistoryAction;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceStatus;
import com.itradingsolutions.itex.api.sales.invoices.models.mapper.InvoiceMapper;
import com.itradingsolutions.itex.api.sales.invoices.models.request.CancelInvoiceRequest;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoiceRepository;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceHistoryService;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoicePrintService;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceStatusService;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceAccessGuard;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceAmountCalculator;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceDetailResolver;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceDueDateCalculator;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceFinder;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceIssueValidator;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceMutationGuard;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceTransitionGuard;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceStatusServiceImpl extends UtilServiceAbs implements IInvoiceStatusService {

    private final IInvoiceRepository repository;
    private final InvoiceMapper mapper;
    private final InvoiceAccessGuard accessGuard;
    private final InvoiceMutationGuard mutationGuard;
    private final InvoiceTransitionGuard transitionGuard;
    private final InvoiceIssueValidator issueValidator;
    private final InvoiceAmountCalculator amountCalculator;
    private final InvoiceDueDateCalculator dueDateCalculator;
    private final InvoiceDetailResolver detailResolver;
    private final InvoiceFinder finder;
    private final ISalesConsecutiveService salesConsecutiveService;
    private final IInvoiceHistoryService historyService;
    private final IInvoicePrintService printService;

    /**
     * A draft that already carries a {@code number} comes from a previous revert: it keeps that
     * number — the guide reserves it permanently on that invoice — and only {@code issuedAt} and
     * {@code dueAt} are recomputed. Totals are recalculated once here and frozen from this point on.
     */
    @Override
    @Transactional
    public InvoiceDTO issue(UUID id) {
        var invoice = loadForTransition(id);
        transitionGuard.assertCanTransition(invoice, InvoiceStatus.ISSUED);

        // Snapshot first so the frozen total and the assigned number show up in the history diff.
        var oldDto = mapper.entityToDTO(invoice);
        amountCalculator.applyTotals(invoice);
        issueValidator.validate(invoice);

        var now = ZonedDateTime.now(zoneId);

        if (invoice.getNumber() == null)
            invoice.setNumber(salesConsecutiveService.generate(SalesConsecutiveType.INV));

        invoice.setStatus(InvoiceStatus.ISSUED);
        invoice.setIssuedAt(now);
        invoice.setDueAt(dueDateCalculator.calculate(now, invoice.getPaymentTerms()));
        invoice.setOverdue(false);
        invoice.setOverdueNotifiedAt(null);
        invoice.setPdfUrl(generatePdfQuietly(invoice));

        return saveAndAudit(invoice, oldDto, InvoiceHistoryAction.ISSUE);
    }

    /**
     * Undoes an issue so the document can be corrected without cancelling it. The {@code number}
     * survives — that is what makes the invoice a "locked draft" that can no longer be deleted —
     * while everything derived from having been issued is cleared, including the stale PDF.
     */
    @Override
    @Transactional
    public InvoiceDTO revertToDraft(UUID id) {
        var invoice = loadForTransition(id);
        transitionGuard.assertCanTransition(invoice, InvoiceStatus.DRAFT);
        transitionGuard.assertNoPayments(invoice.getId());

        var oldDto = mapper.entityToDTO(invoice);

        invoice.setStatus(InvoiceStatus.DRAFT);
        invoice.setIssuedAt(null);
        invoice.setDueAt(null);
        invoice.setOverdue(false);
        invoice.setOverdueNotifiedAt(null);
        invoice.setPdfUrl(null);

        return saveAndAudit(invoice, oldDto, InvoiceHistoryAction.REVERT_TO_DRAFT);
    }

    /**
     * Terminal state: the invoice is never physically deleted, so it keeps its number and its
     * reason for the audit trail. The lock is released because {@code CANCELLED} is not lockable
     * and nobody should keep the tab occupied by a closed document.
     */
    @Override
    @Transactional
    public InvoiceDTO cancel(UUID id, CancelInvoiceRequest request) {
        var invoice = loadForTransition(id);
        transitionGuard.assertCanTransition(invoice, InvoiceStatus.CANCELLED);
        transitionGuard.assertNoPayments(invoice.getId());

        var oldDto = mapper.entityToDTO(invoice);

        invoice.setStatus(InvoiceStatus.CANCELLED);
        invoice.setCancelledAt(ZonedDateTime.now(zoneId));
        invoice.setCancelReason(request.cancelReason());
        invoice.setOverdue(false);
        invoice.setOverdueNotifiedAt(null);

        // Released on the entity rather than with a bulk update, so the returned detail already
        // shows the invoice as closed instead of reporting a lock that no longer exists.
        invoice.setOpenBy(null);
        invoice.setOpenAt(null);

        return saveAndAudit(invoice, oldDto, InvoiceHistoryAction.CANCEL);
    }

    /**
     * Same guard order every mutating invoice flow uses: the caller must be allowed to see it, own
     * it, and hold its lock. {@code assertEditable} is deliberately absent — it only admits
     * {@code DRAFT}, and issuing/cancelling act on other statuses by definition.
     */
    private InvoiceEntity loadForTransition(UUID id) {
        var invoice = finder.findDetailById(id);
        accessGuard.assertCanAccess(invoice);
        accessGuard.assertCanMutate(invoice);
        mutationGuard.assertLockedByCurrentUser(invoice);
        return invoice;
    }

    /**
     * The official PDF is produced as part of issuing, but a failure here does not undo the
     * emission: the invoice is already numbered and its financial content is frozen, so
     * {@code print} can rebuild the exact same document later. Aborting instead would let a report
     * template problem block the whole billing flow.
     */
    private String generatePdfQuietly(InvoiceEntity invoice) {
        try {
            return printService.generateFinal(invoice);
        } catch (RuntimeException ex) {
            log.error("Could not generate the PDF of invoice {}; it will be generated on the next print", invoice.getId(), ex);
            return null;
        }
    }

    private InvoiceDTO saveAndAudit(InvoiceEntity invoice, InvoiceDTO oldDto, InvoiceHistoryAction action) {
        var newDto = detailResolver.resolve(repository.save(invoice));
        historyService.addHistory(action, oldDto, newDto);
        return newDto;
    }
}
