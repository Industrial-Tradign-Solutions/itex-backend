package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceDTO;

/**
 * Result of an open/lock attempt: the current invoice state plus whether the caller now holds
 * (or is allowed to view under) an editable lock. Internal to the service layer — the controller
 * maps it into {@code OpenLockInvoiceResponse}.
 */
public record InvoiceLockResult(InvoiceDTO invoice, boolean isValidOpen) {
}
