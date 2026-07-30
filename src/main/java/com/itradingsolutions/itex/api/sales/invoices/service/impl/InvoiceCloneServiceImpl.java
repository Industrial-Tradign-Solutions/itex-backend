package com.itradingsolutions.itex.api.sales.invoices.service.impl;

import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import com.itradingsolutions.itex.api.common.salesconsecutive.models.enums.SalesConsecutiveType;
import com.itradingsolutions.itex.api.common.salesconsecutive.services.ISalesConsecutiveService;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.sales.invoices.exceptions.InvoiceMaxOpenException;
import com.itradingsolutions.itex.api.sales.invoices.exceptions.NotExistInvoiceException;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceChargeEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceClonedEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceIpPoEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceIpProductEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceTaxEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceHistoryAction;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceStatus;
import com.itradingsolutions.itex.api.sales.invoices.models.mapper.InvoiceMapper;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoiceClonedRepository;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoiceIpPoRepository;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoiceRepository;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceCloneService;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceHistoryService;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceAccessGuard;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceAmountCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class InvoiceCloneServiceImpl extends UtilServiceAbs implements IInvoiceCloneService {

    private final IInvoiceRepository repository;
    private final IInvoiceClonedRepository clonedRepository;
    private final IInvoiceIpPoRepository linkedPoRepository;
    private final InvoiceMapper mapper;
    private final InvoiceAccessGuard guard;
    private final InvoiceAmountCalculator calculator;
    private final IUserService userService;
    private final ISalesConsecutiveService salesConsecutiveService;
    private final IInvoiceHistoryService invoiceHistoryService;

    @Override
    @Transactional
    public InvoiceDTO clone(UUID id) {
        var original = findById(id);
        guard.assertCanAccess(original);

        var user = userService.getUserAuthenticated();
        validateMaxOpen(user.getId());

        var now = ZonedDateTime.now(zoneId);
        var clone = mapper.clone(original);
        resetForDraft(clone, user, now);

        cloneChildren(original.getProducts(), clone.getProducts(), InvoiceCloneServiceImpl::cloneProduct,
                item -> item.setInvoice(clone));
        cloneChildren(original.getCharges(), clone.getCharges(), InvoiceCloneServiceImpl::cloneCharge,
                item -> item.setInvoice(clone));
        cloneChildren(original.getTaxes(), clone.getTaxes(), InvoiceCloneServiceImpl::cloneTax,
                item -> item.setInvoice(clone));

        clone.setTotalAmount(calculator.totalAmount(clone));

        var savedClone = repository.save(clone);

        cloneLinkedPurchaseOrders(original, savedClone);

        var clonedItem = new InvoiceClonedEntity();
        clonedItem.setId(original.getId(), savedClone.getId());
        clonedItem.setMainInvoice(original);
        clonedItem.setClonedInvoice(savedClone);
        clonedRepository.save(clonedItem);

        var originalDto = mapper.entityToDTO(original);
        var cloneDto = mapper.entityToDTO(savedClone);
        invoiceHistoryService.addHistory(InvoiceHistoryAction.CLONE, originalDto, cloneDto);

        return cloneDto;
    }

    private void resetForDraft(InvoiceEntity clone, UserEntity user, ZonedDateTime now) {
        clone.setStatus(InvoiceStatus.DRAFT);
        clone.setDraftNumber(salesConsecutiveService.generate(SalesConsecutiveType.DRAFT_INV));
        clone.setNumber(null);
        clone.setPaidAmount(BigDecimal.ZERO);
        clone.setPdfUrl(null);
        clone.setOverdue(false);
        clone.setDueAt(null);
        clone.setIssuedAt(null);
        clone.setPartialPaidAt(null);
        clone.setPaidAt(null);
        clone.setCancelledAt(null);
        clone.setCancelReason(null);
        clone.setOverdueNotifiedAt(null);
        clone.setSalesRep(user);
        clone.setOpenBy(user);
        clone.setOpenAt(now);
        clone.setProducts(new ArrayList<>());
        clone.setCharges(new ArrayList<>());
        clone.setTaxes(new ArrayList<>());
        clone.setPayments(new ArrayList<>());
        clone.setLinkedPurchaseOrders(new ArrayList<>());
        clone.setClonedInvoices(new ArrayList<>());
    }

    private void cloneLinkedPurchaseOrders(InvoiceEntity original, InvoiceEntity clone) {
        Optional.ofNullable(original.getLinkedPurchaseOrders())
                .orElseGet(Collections::emptyList)
                .forEach(link -> {
                    var newLink = new InvoiceIpPoEntity();
                    newLink.setId(clone.getId(), link.getPurchaseOrder().getId());
                    newLink.setInvoice(clone);
                    newLink.setPurchaseOrder(link.getPurchaseOrder());
                    linkedPoRepository.save(newLink);
                });
    }

    private void validateMaxOpen(UUID userId) {
        if (repository.countByOpenUserId(userId) >= maxTabsOpen)
            throw new InvoiceMaxOpenException(compositeMessage("sales.invoice.not-open-max", new String[]{maxTabsOpen.toString()}));
    }

    private InvoiceEntity findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NotExistInvoiceException(simpleMessage("sales.invoice.not-exist")));
    }

    private static <T> void cloneChildren(List<T> source, List<T> target, Function<T, T> cloner, Consumer<T> linker) {
        Optional.ofNullable(source).orElseGet(Collections::emptyList).forEach(item -> {
            var cloneItem = cloner.apply(item);
            linker.accept(cloneItem);
            target.add(cloneItem);
        });
    }

    private static InvoiceIpProductEntity cloneProduct(InvoiceIpProductEntity source) {
        var target = new InvoiceIpProductEntity();
        target.setProduct(source.getProduct());
        target.setNumber(source.getNumber());
        target.setQuantity(source.getQuantity());
        target.setUnitType(source.getUnitType());
        target.setLeadTime(source.getLeadTime());
        target.setLeadTimeType(source.getLeadTimeType());
        target.setUnitPrice(source.getUnitPrice());
        target.setProfitMargin(source.getProfitMargin());
        target.setCondition(source.getCondition());
        return target;
    }

    private static InvoiceChargeEntity cloneCharge(InvoiceChargeEntity source) {
        var target = new InvoiceChargeEntity();
        target.setDescription(source.getDescription());
        target.setType(source.getType());
        target.setValue(source.getValue());
        return target;
    }

    private static InvoiceTaxEntity cloneTax(InvoiceTaxEntity source) {
        var target = new InvoiceTaxEntity();
        target.setType(source.getType());
        target.setDescription(source.getDescription());
        target.setRate(source.getRate());
        target.setTaxableBase(source.getTaxableBase());
        target.setValue(source.getValue());
        return target;
    }
}
