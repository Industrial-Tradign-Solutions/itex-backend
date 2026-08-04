package com.itradingsolutions.itex.api.sales.invoices.models.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Body of {@code POST /sales/invoice/{id}/product/import-from-po}. Copies the selected PO line items
 * into the invoice's own product table. The PO must already be linked to the invoice
 * ({@code t_invoice_ip_po}); {@code poProductIds} are the ids of the PO products to bring over.
 */
public record ImportInvoiceProductsRequest(

        @NotNull(message = "Purchase order is required")
        UUID poId,

        @NotEmpty(message = "At least one PO product is required")
        List<UUID> poProductIds
) {}
