package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceTaxDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.request.InvoiceTaxRequest;

import java.util.UUID;

public interface IInvoiceTaxService {

    InvoiceTaxDTO create(UUID invoiceId, InvoiceTaxRequest request);

    InvoiceTaxDTO update(UUID invoiceId, UUID taxId, InvoiceTaxRequest request);

    void remove(UUID invoiceId, UUID taxId);

    InvoiceTaxDTO get(UUID invoiceId, UUID taxId);
}
