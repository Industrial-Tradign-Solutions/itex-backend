package com.itradingsolutions.itex.api.sales.invoices.service.impl;

import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceTaxDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceTaxEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceHistoryAction;
import com.itradingsolutions.itex.api.sales.invoices.models.mapper.InvoiceTaxMapper;
import com.itradingsolutions.itex.api.sales.invoices.models.request.InvoiceTaxRequest;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoiceRepository;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceHistoryService;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceTaxService;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceAmountCalculator;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceChildMutationSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceTaxServiceImpl extends UtilServiceAbs implements IInvoiceTaxService {

    private final InvoiceChildMutationSupport support;
    private final IInvoiceRepository repository;
    private final InvoiceTaxMapper mapper;
    private final InvoiceAmountCalculator calculator;
    private final IInvoiceHistoryService historyService;

    @Override
    @Transactional
    public InvoiceTaxDTO create(UUID invoiceId, InvoiceTaxRequest request) {
        var invoice = support.loadEditable(invoiceId);
        var dto = mapper.requestToDTO(request);

        var entity = new InvoiceTaxEntity();
        entity.setInvoice(invoice);
        applyFields(entity, dto);
        invoice.getTaxes().add(entity);

        calculator.applyTotals(invoice);
        repository.save(invoice);

        var savedDto = mapper.entityToDTO(entity);
        historyService.addHistoryTax(InvoiceHistoryAction.ADD_TAX, null, savedDto, invoiceId);
        return savedDto;
    }

    @Override
    @Transactional
    public InvoiceTaxDTO update(UUID invoiceId, UUID taxId, InvoiceTaxRequest request) {
        var invoice = support.loadEditable(invoiceId);
        var entity = findChild(invoice, taxId);
        var oldDto = mapper.entityToDTO(entity);

        applyFields(entity, mapper.requestToDTO(request));

        calculator.applyTotals(invoice);
        repository.save(invoice);

        var newDto = mapper.entityToDTO(entity);
        historyService.addHistoryTax(InvoiceHistoryAction.UPDATE_TAX, oldDto, newDto, invoiceId);
        return newDto;
    }

    @Override
    @Transactional
    public void remove(UUID invoiceId, UUID taxId) {
        var invoice = support.loadEditable(invoiceId);
        var entity = findChild(invoice, taxId);
        var oldDto = mapper.entityToDTO(entity);

        invoice.getTaxes().removeIf(t -> t.getId().equals(taxId));

        calculator.applyTotals(invoice);
        repository.save(invoice);

        historyService.addHistoryTax(InvoiceHistoryAction.REMOVE_TAX, oldDto, null, invoiceId);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceTaxDTO get(UUID invoiceId, UUID taxId) {
        var invoice = support.loadReadable(invoiceId);
        return mapper.entityToDTO(findChild(invoice, taxId));
    }

    /**
     * Rate, taxable base and value are stored exactly as received; the backend does not recompute
     * {@code value = rate * taxableBase} (guide decision: taxes are entered manually).
     */
    private void applyFields(InvoiceTaxEntity entity, InvoiceTaxDTO dto) {
        entity.setType(dto.getType());
        entity.setDescription(dto.getDescription());
        entity.setRate(dto.getRate());
        entity.setTaxableBase(dto.getTaxableBase());
        entity.setValue(dto.getValue());
    }

    private InvoiceTaxEntity findChild(InvoiceEntity invoice, UUID taxId) {
        return invoice.getTaxes().stream()
                .filter(t -> t.getId().equals(taxId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(simpleMessage("sales.invoice.tax.not-exist")));
    }
}
