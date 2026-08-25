package com.itradingsolutions.itex.api.sales.invoices.models.response;

import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.ip.products.models.enums.ProductCondition;
import com.itradingsolutions.itex.api.ip.products.models.responses.IpProductResponse;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Invoice line. Mirrors the shape the frontend already renders for Quote Request lines, except
 * that {@code ipProduct} arrives without {@code surplus}, {@code substituteProduct} and
 * {@code openBy} — none of them belong on an invoice.
 */
public record InvoiceProductResponse(
        UUID id,
        IpProductResponse ipProduct,
        Integer number,
        BigDecimal quantity,
        String unitType,
        Integer leadTime,
        LeadTime leadTimeType,
        BigDecimal unitPrice,
        BigDecimal profitMargin,
        ProductCondition condition,
        BigDecimal extendedPrice,
        BigDecimal sellingUnitPrice,
        BigDecimal sellingExtendedPrice
) {
}
