package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceDTO;

import java.util.UUID;

public interface IInvoiceCloneService {

    InvoiceDTO clone(UUID id);
}
