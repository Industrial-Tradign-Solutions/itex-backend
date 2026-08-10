package com.itradingsolutions.itex.api.sales.invoices.models.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Body of {@code PATCH /sales/invoice/{id}/payment/{payment_id}/void}. A registered payment is
 * never edited or deleted — it is voided with a reason and replaced by a new one (guide §7).
 */
public record VoidInvoicePaymentRequest(

        @NotBlank(message = "Voided reason is required")
        @Size(max = 1000, message = "The voided reason cannot exceed 1000 characters")
        String voidedReason
) {}
