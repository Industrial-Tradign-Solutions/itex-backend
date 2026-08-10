package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;

import java.util.UUID;

/**
 * PDF of an Invoice. Two modes, per the invoicing guide §4: a {@code DRAFT} is regenerated on every
 * print — it is a preview of something still being edited — while issuing produces the official
 * document, which is stored and served unchanged from then on.
 */
public interface IInvoicePrintService {

    /** Bytes of the PDF, generating it when there is nothing stored to serve. */
    byte[] print(UUID id);

    /**
     * Generates the official PDF of an invoice being issued and returns its absolute path.
     * Called from within the issue transaction.
     */
    String generateFinal(InvoiceEntity invoice);
}
