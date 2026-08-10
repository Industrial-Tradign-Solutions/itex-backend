package com.itradingsolutions.itex.api.sales.invoices.service;

import java.util.UUID;

public interface IInvoiceDeleteService {

    /**
     * Physically deletes a draft that was never issued, returning its {@code draft_number} to the
     * free list. A draft that already holds a {@code number} (reverted from {@code ISSUED}) is
     * locked forever and cannot be deleted, permission or not (invoicing guide §4).
     */
    void delete(UUID id);
}
