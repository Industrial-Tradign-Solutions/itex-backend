package com.itradingsolutions.itex.api.sales.invoices.service;

/**
 * Maintenance of the {@code is_overdue} flag. Being overdue is not a status (invoicing guide §4):
 * an invoice can be overdue while {@code ISSUED} or while {@code PARTIAL_PAID}, and that difference
 * matters, so it lives in its own column instead of overwriting the lifecycle.
 */
public interface IInvoiceOverdueService {

    /** Recomputes the flag in both directions. Returns how many rows changed. */
    int refreshOverdueFlags();

    /** Notifies the sales rep of each newly overdue invoice, once. Returns how many were notified. */
    int notifyOverdue();

    /**
     * Weekly reminder: re-notifies every invoice still overdue, whether or not it was reported
     * before. Returns how many were notified.
     */
    int remindOverdue();
}
