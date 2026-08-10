package com.itradingsolutions.itex.api.sales.invoices.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Body of {@code PATCH /sales/invoice/{id}/cancel}. A cancelled invoice is never deleted, so the
 * reason is what explains the row to whoever audits it later — hence mandatory (invoicing guide §4).
 */
public record CancelInvoiceRequest(

        @NotBlank(message = "Cancel reason is required")
        @Size(max = 1000, message = "The cancel reason cannot exceed 1000 characters")
        String cancelReason
) {}
