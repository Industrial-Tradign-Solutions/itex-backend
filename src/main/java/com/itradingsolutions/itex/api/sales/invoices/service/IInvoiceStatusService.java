package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.request.CancelInvoiceRequest;

import java.util.UUID;

/**
 * Manual status transitions of an Invoice (invoicing guide §4). Payment-derived transitions
 * ({@code PARTIAL_PAID}/{@code PAID}) belong to {@link IInvoicePaymentService} instead — both go
 * through {@link InvoiceTransitionGuard}.
 */
public interface IInvoiceStatusService {

    /** {@code DRAFT → ISSUED}: assigns the final number the first time and freezes the totals. */
    InvoiceDTO issue(UUID id);

    /** {@code ISSUED → DRAFT}: only without payments; the assigned number stays reserved forever. */
    InvoiceDTO revertToDraft(UUID id);

    /** {@code DRAFT|ISSUED → CANCELLED}: final, reason mandatory, {@code ISSUED} only without payments. */
    InvoiceDTO cancel(UUID id, CancelInvoiceRequest request);
}
