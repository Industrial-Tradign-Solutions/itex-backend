package com.itradingsolutions.itex.api.sales.invoices.models.response;

import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.ip.products.models.enums.ProductCondition;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * One importable product line from a PO linked to the invoice. Use {@code poProductId} in
 * {@link com.itradingsolutions.itex.api.sales.invoices.models.request.ImportInvoiceProductsRequest#poProductIds()}
 * to import the selected lines.
 *
 * Products whose {@code productId} is already present in the invoice are excluded.
 */
public record AvailablePoProductResponse(
        UUID poId,
        String poNumber,
        UUID poProductId,
        UUID productId,
        String productDescription,
        String productMfrReference,
        BigDecimal quantity,
        String unitType,
        Integer leadTime,
        LeadTime leadTimeType,
        BigDecimal unitPrice,
        BigDecimal profitMargin,
        ProductCondition condition
) {
}
