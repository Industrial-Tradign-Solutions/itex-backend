package com.itradingsolutions.itex.api.sales.invoices.service.impl;

import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.ip.po.service.IIpPurchaseOrderService;
import com.itradingsolutions.itex.api.ip.po.models.dto.IpPurchaseOrderDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceChargeDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceChargeEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceTaxEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceChargeType;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceHistoryAction;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceTaxType;
import com.itradingsolutions.itex.api.sales.invoices.models.mapper.InvoiceChargeMapper;
import com.itradingsolutions.itex.api.sales.invoices.models.mapper.InvoiceTaxMapper;
import com.itradingsolutions.itex.api.sales.invoices.models.request.ImportInvoiceChargesRequest;
import com.itradingsolutions.itex.api.sales.invoices.models.request.InvoiceChargeRequest;
import com.itradingsolutions.itex.api.sales.invoices.models.response.AvailablePoChargeResponse;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoiceRepository;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceChargeService;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceHistoryService;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceAmountCalculator;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceChildMutationSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceChargeServiceImpl extends UtilServiceAbs implements IInvoiceChargeService {

    private static final int DESCRIPTION_MAX = 100;

    private final InvoiceChildMutationSupport support;
    private final IInvoiceRepository repository;
    private final InvoiceChargeMapper mapper;
    private final InvoiceTaxMapper taxMapper;
    private final InvoiceAmountCalculator calculator;
    private final IInvoiceHistoryService historyService;
    private final IIpPurchaseOrderService purchaseOrderService;

    @Override
    @Transactional
    public InvoiceChargeDTO create(UUID invoiceId, InvoiceChargeRequest request) {
        var invoice = support.loadEditable(invoiceId);
        var dto = mapper.requestToDTO(request);

        var entity = new InvoiceChargeEntity();
        entity.setInvoice(invoice);
        entity.setDescription(dto.getDescription());
        entity.setType(dto.getType());
        entity.setValue(dto.getValue());
        invoice.getCharges().add(entity);

        calculator.applyTotals(invoice);
        repository.save(invoice);

        var savedDto = mapper.entityToDTO(entity);
        historyService.addHistoryCharge(InvoiceHistoryAction.ADD_CHARGE, null, savedDto, invoiceId);
        return savedDto;
    }

    @Override
    @Transactional
    public InvoiceChargeDTO update(UUID invoiceId, UUID chargeId, InvoiceChargeRequest request) {
        var invoice = support.loadEditable(invoiceId);
        var entity = findChild(invoice, chargeId);
        var oldDto = mapper.entityToDTO(entity);

        var dto = mapper.requestToDTO(request);
        entity.setDescription(dto.getDescription());
        entity.setType(dto.getType());
        entity.setValue(dto.getValue());

        calculator.applyTotals(invoice);
        repository.save(invoice);

        var newDto = mapper.entityToDTO(entity);
        historyService.addHistoryCharge(InvoiceHistoryAction.UPDATE_CHARGE, oldDto, newDto, invoiceId);
        return newDto;
    }

    @Override
    @Transactional
    public void remove(UUID invoiceId, UUID chargeId) {
        var invoice = support.loadEditable(invoiceId);
        var entity = findChild(invoice, chargeId);
        var oldDto = mapper.entityToDTO(entity);

        invoice.getCharges().removeIf(c -> c.getId().equals(chargeId));

        calculator.applyTotals(invoice);
        repository.save(invoice);

        historyService.addHistoryCharge(InvoiceHistoryAction.REMOVE_CHARGE, oldDto, null, invoiceId);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceChargeDTO get(UUID invoiceId, UUID chargeId) {
        var invoice = support.loadReadable(invoiceId);
        return mapper.entityToDTO(findChild(invoice, chargeId));
    }

    @Override
    @Transactional
    public List<InvoiceChargeDTO> importFromPo(UUID invoiceId, ImportInvoiceChargesRequest request) {
        var invoice = support.loadEditable(invoiceId);
        support.assertPoLinked(invoice, request.poId());

        var po = purchaseOrderService.findById(request.poId());

        // PO charges carry no type classification (only description + value), so all land as OTHER.
        var addedCharges = new ArrayList<InvoiceChargeEntity>();
        for (var view : po.getAllOtherCharges()) {
            var entity = new InvoiceChargeEntity();
            entity.setInvoice(invoice);
            entity.setDescription(truncate(view.description()));
            entity.setType(InvoiceChargeType.OTHER);
            entity.setValue(view.value() != null ? view.value() : BigDecimal.ZERO);
            invoice.getCharges().add(entity);
            addedCharges.add(entity);
        }

        // The PO header salesTax has no rate/base of its own; it comes over as a tax row carrying
        // only its amount (guide decision: salesTax imports as a tax, not a charge).
        InvoiceTaxEntity importedTax = null;
        var salesTax = po.getSalesTax();
        if (salesTax != null && salesTax.compareTo(BigDecimal.ZERO) > 0) {
            importedTax = new InvoiceTaxEntity();
            importedTax.setInvoice(invoice);
            importedTax.setType(InvoiceTaxType.US_SALES_TAX);
            importedTax.setDescription("Sales Tax (imported)");
            importedTax.setRate(BigDecimal.ZERO);
            importedTax.setTaxableBase(BigDecimal.ZERO);
            importedTax.setValue(salesTax);
            invoice.getTaxes().add(importedTax);
        }

        calculator.applyTotals(invoice);
        repository.save(invoice);

        var results = new ArrayList<InvoiceChargeDTO>();
        for (var entity : addedCharges) {
            var dto = mapper.entityToDTO(entity);
            results.add(dto);
            historyService.addHistoryCharge(InvoiceHistoryAction.ADD_CHARGE, null, dto, invoiceId);
        }
        if (importedTax != null)
            historyService.addHistoryTax(InvoiceHistoryAction.ADD_TAX, null, taxMapper.entityToDTO(importedTax), invoiceId);

        return results;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailablePoChargeResponse> listAvailableFromPos(UUID invoiceId) {
        var invoice = support.loadReadable(invoiceId);

        var result = new ArrayList<AvailablePoChargeResponse>();
        for (var link : Optional.ofNullable(invoice.getLinkedPurchaseOrders()).orElseGet(Collections::emptyList)) {
            var po = purchaseOrderService.findById(link.getPurchaseOrder().getId());
            var poId = link.getPurchaseOrder().getId();
            var poNumber = po.getNumber();

            for (var view : po.getAllOtherCharges()) {
                result.add(new AvailablePoChargeResponse(
                        poId,
                        poNumber,
                        view.description(),
                        view.value() != null ? view.value() : BigDecimal.ZERO,
                        view.source()
                ));
            }

            // salesTax is shown separately so the user knows it will land as a tax row on import.
            var salesTax = po.getSalesTax();
            if (salesTax != null && salesTax.compareTo(BigDecimal.ZERO) > 0) {
                result.add(new AvailablePoChargeResponse(
                        poId,
                        poNumber,
                        "Sales Tax",
                        salesTax,
                        "SALES_TAX"
                ));
            }
        }
        return result;
    }

    private String truncate(String description) {
        if (description == null)
            return "";
        return description.length() > DESCRIPTION_MAX ? description.substring(0, DESCRIPTION_MAX) : description;
    }

    private InvoiceChargeEntity findChild(InvoiceEntity invoice, UUID chargeId) {
        return invoice.getCharges().stream()
                .filter(c -> c.getId().equals(chargeId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(simpleMessage("sales.invoice.charge.not-exist")));
    }
}
