package com.itradingsolutions.itex.api.ip.q.models.requests;

import com.itradingsolutions.itex.api.ip.products.models.enums.ProductCondition;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record IpQuotationProductRequest(

        @NotNull(message = "Quotation Quote Request reference is required")
        UUID quotationsQuoteRequestId,

        @NotNull(message = "Product is required")
        UUID quoteRequestProductId,

        @NotNull(message = "Profit margin is required")
        BigDecimal profitMargin,

        @NotNull(message = "Condition is required")
        ProductCondition condition
) {
}
