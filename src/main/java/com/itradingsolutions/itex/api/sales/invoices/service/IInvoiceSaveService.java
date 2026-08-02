package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.request.CreateInvoiceRequest;
import com.itradingsolutions.itex.api.sales.invoices.models.request.UpdateInvoiceRequest;

import java.util.UUID;

/**
 * Write side of the Invoice module: creating a draft and editing it. Reading, locking and cloning
 * live in their own services.
 */
public interface IInvoiceSaveService {

    /**
     * Creates a {@code DRAFT} invoice already locked by the authenticated user, who also becomes
     * its sales rep. The ship to block is copied from the Client.
     */
    InvoiceDTO create(CreateInvoiceRequest request);

    /**
     * Edits a {@code DRAFT} invoice held by the authenticated user. Never touches numbering,
     * status, department, amounts or any lifecycle timestamp.
     */
    InvoiceDTO update(UUID id, UpdateInvoiceRequest request);
}
