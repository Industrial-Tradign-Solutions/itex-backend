package com.itradingsolutions.itex.api.sales.invoices.models.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Body of {@code POST /sales/invoice/{id}/charge/import-from-po}. Copies every other-charge of the
 * linked PO (own + imported-from-quotation + imported-from-QR) into the invoice as {@code OTHER}
 * charges, and the PO header {@code salesTax}, when positive, as a tax row.
 */
public record ImportInvoiceChargesRequest(

        @NotNull(message = "Purchase order is required")
        UUID poId
) {}
