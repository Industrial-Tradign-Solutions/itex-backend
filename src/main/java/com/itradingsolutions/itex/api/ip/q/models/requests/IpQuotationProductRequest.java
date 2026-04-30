package com.itradingsolutions.itex.api.ip.q.models.requests;

import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationProductCondition;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record IpQuotationProductRequest(

        @NotNull(message = "Quotation Quote Request reference is required")
        UUID quotationsQuoteRequestId,

        UUID quoteRequestProductId,

        @NotNull(message = "Profit margin is required")
        BigDecimal profitMargin,

        @NotNull(message = "Condition is required")
        IpQuotationProductCondition condition
) {
}
