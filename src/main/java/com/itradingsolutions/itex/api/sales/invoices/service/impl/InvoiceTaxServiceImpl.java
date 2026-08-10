package com.itradingsolutions.itex.api.sales.invoices.service.impl;

import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.sales.invoices.models.InvoiceMoney;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceTaxDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceTaxEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceHistoryAction;
import com.itradingsolutions.itex.api.sales.invoices.models.mapper.InvoiceTaxMapper;
import com.itradingsolutions.itex.api.sales.invoices.models.request.InvoiceTaxRequest;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceHistoryService;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceTaxService;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceChildMutationSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceTaxServiceImpl extends UtilServiceAbs implements IInvoiceTaxService {

    private static final String NOT_EXIST_KEY = "sales.invoice.tax.not-exist";

    private final InvoiceChildMutationSupport support;
    private final InvoiceTaxMapper mapper;
    private final IInvoiceHistoryService historyService;

    @Override
    @Transactional
    public InvoiceTaxDTO create(UUID invoiceId, InvoiceTaxRequest request) {
        var invoice = support.loadEditable(invoiceId);

        var entity = new InvoiceTaxEntity();
        entity.setInvoice(invoice);
        applyFields(entity, mapper.requestToDTO(request));
        invoice.getTaxes().add(entity);

        support.saveWithTotals(invoice);

        var savedDto = mapper.entityToDTO(entity);
        historyService.addHistoryTax(InvoiceHistoryAction.ADD_TAX, null, savedDto, invoiceId);
        return savedDto;
    }

    @Override
    @Transactional
    public InvoiceTaxDTO update(UUID invoiceId, UUID taxId, InvoiceTaxRequest request) {
        var invoice = support.loadEditable(invoiceId);
        var entity = support.findChild(invoice.getTaxes(), taxId, NOT_EXIST_KEY);
        var oldDto = mapper.entityToDTO(entity);

        applyFields(entity, mapper.requestToDTO(request));
        support.saveWithTotals(invoice);

        var newDto = mapper.entityToDTO(entity);
        historyService.addHistoryTax(InvoiceHistoryAction.UPDATE_TAX, oldDto, newDto, invoiceId);
        return newDto;
    }

    @Override
    @Transactional
    public void remove(UUID invoiceId, UUID taxId) {
        var invoice = support.loadEditable(invoiceId);
        var entity = support.findChild(invoice.getTaxes(), taxId, NOT_EXIST_KEY);
        var oldDto = mapper.entityToDTO(entity);

        invoice.getTaxes().remove(entity);
        support.saveWithTotals(invoice);

        historyService.addHistoryTax(InvoiceHistoryAction.REMOVE_TAX, oldDto, null, invoiceId);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceTaxDTO get(UUID invoiceId, UUID taxId) {
        var invoice = support.loadReadable(invoiceId);
        return mapper.entityToDTO(support.findChild(invoice.getTaxes(), taxId, NOT_EXIST_KEY));
    }

    /**
     * The taxable base is the caller's decision — the backend does not recompute it from the
     * products subtotal — but the amount charged is computed here as {@code taxableBase * rate},
     * never received, so the arithmetic is done once and in {@code BigDecimal} (guide §5; rounding
     * to 2 decimals belongs to the PDF alone).
     */
    private void applyFields(InvoiceTaxEntity entity, InvoiceTaxDTO dto) {
        entity.setType(dto.getType());
        entity.setDescription(dto.getDescription());
        entity.setRate(dto.getRate());
        entity.setTaxableBase(dto.getTaxableBase());
        entity.setValue(InvoiceMoney.scaled(dto.getTaxableBase().multiply(dto.getRate())));
    }
}
