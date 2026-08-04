package com.itradingsolutions.itex.api.sales.invoices.models.response;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * One charge (or the salesTax) from a PO linked to the invoice. Charges are not deduplicated;
 * the full list from each PO is returned. Use {@code poId} in
 * {@link com.itradingsolutions.itex.api.sales.invoices.models.request.ImportInvoiceChargesRequest}
 * to import all charges from a given PO.
 *
 * {@code source} is one of {@code OWN}, {@code QUOTATION}, {@code QUOTATION_QR}, or {@code SALES_TAX}.
 */
public record AvailablePoChargeResponse(
        UUID poId,
        String poNumber,
        String description,
        BigDecimal value,
        String source
) {
}
