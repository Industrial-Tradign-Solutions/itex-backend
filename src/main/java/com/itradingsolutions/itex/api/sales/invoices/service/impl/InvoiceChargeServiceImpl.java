package com.itradingsolutions.itex.api.sales.invoices.service.impl;

import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.ip.po.service.IIpPurchaseOrderService;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceChargeDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceChargeEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceTaxEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceChargeType;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceHistoryAction;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceTaxType;
import com.itradingsolutions.itex.api.sales.invoices.models.mapper.InvoiceChargeMapper;
import com.itradingsolutions.itex.api.sales.invoices.models.mapper.InvoiceTaxMapper;
import com.itradingsolutions.itex.api.sales.invoices.models.request.ImportInvoiceChargesRequest;
import com.itradingsolutions.itex.api.sales.invoices.models.request.InvoiceChargeRequest;
import com.itradingsolutions.itex.api.sales.invoices.models.response.AvailablePoChargeResponse;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceChargeService;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceHistoryService;
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
    private static final String NOT_EXIST_KEY = "sales.invoice.charge.not-exist";

    private final InvoiceChildMutationSupport support;
    private final InvoiceChargeMapper mapper;
    private final InvoiceTaxMapper taxMapper;
    private final IInvoiceHistoryService historyService;
    private final IIpPurchaseOrderService purchaseOrderService;

    @Override
    @Transactional
    public InvoiceChargeDTO create(UUID invoiceId, InvoiceChargeRequest request) {
        var invoice = support.loadEditable(invoiceId);

        var entity = new InvoiceChargeEntity();
        entity.setInvoice(invoice);
        applyFields(entity, mapper.requestToDTO(request));
        support.persist(entity);
        invoice.getCharges().add(entity);

        support.saveWithTotals(invoice);

        var savedDto = mapper.entityToDTO(entity);
        historyService.addHistoryCharge(InvoiceHistoryAction.ADD_CHARGE, null, savedDto, invoiceId);
        return savedDto;
    }

    @Override
    @Transactional
    public InvoiceChargeDTO update(UUID invoiceId, UUID chargeId, InvoiceChargeRequest request) {
        var invoice = support.loadEditable(invoiceId);
        var entity = support.findChild(invoice.getCharges(), chargeId, NOT_EXIST_KEY);
        var oldDto = mapper.entityToDTO(entity);

        applyFields(entity, mapper.requestToDTO(request));
        support.saveWithTotals(invoice);

        var newDto = mapper.entityToDTO(entity);
        historyService.addHistoryCharge(InvoiceHistoryAction.UPDATE_CHARGE, oldDto, newDto, invoiceId);
        return newDto;
    }

    @Override
    @Transactional
    public void remove(UUID invoiceId, UUID chargeId) {
        var invoice = support.loadEditable(invoiceId);
        var entity = support.findChild(invoice.getCharges(), chargeId, NOT_EXIST_KEY);
        var oldDto = mapper.entityToDTO(entity);

        invoice.getCharges().remove(entity);
        support.saveWithTotals(invoice);

        historyService.addHistoryCharge(InvoiceHistoryAction.REMOVE_CHARGE, oldDto, null, invoiceId);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceChargeDTO get(UUID invoiceId, UUID chargeId) {
        var invoice = support.loadReadable(invoiceId);
        return mapper.entityToDTO(support.findChild(invoice.getCharges(), chargeId, NOT_EXIST_KEY));
    }

    private void applyFields(InvoiceChargeEntity entity, InvoiceChargeDTO dto) {
        entity.setDescription(dto.getDescription());
        entity.setType(dto.getType());
        entity.setValue(dto.getValue());
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
            support.persist(entity);
            invoice.getCharges().add(entity);
            addedCharges.add(entity);
        }

        // The PO header salesTax has no rate/base of its own; it comes over as a tax row carrying
        // only its amount (guide decision: salesTax imports as a tax, not a charge). This is the one
        // place a tax value is not `taxableBase * rate` — editing the row through the tax endpoint
        // recomputes it from whatever rate and base the user then supplies.
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
            support.persist(importedTax);
            invoice.getTaxes().add(importedTax);
        }

        support.saveWithTotals(invoice);

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
}
