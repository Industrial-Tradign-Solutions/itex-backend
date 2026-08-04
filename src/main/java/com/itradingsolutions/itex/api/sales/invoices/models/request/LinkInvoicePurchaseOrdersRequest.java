package com.itradingsolutions.itex.api.sales.invoices.models.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

/**
 * Body of {@code POST /sales/invoice/{id}/purchase-order}. Links one or more POs to the invoice for
 * traceability ({@code t_invoice_ip_po}); already-linked ids are skipped.
 */
public record LinkInvoicePurchaseOrdersRequest(

        @NotEmpty(message = "At least one purchase order is required")
        List<UUID> poIds
) {}
