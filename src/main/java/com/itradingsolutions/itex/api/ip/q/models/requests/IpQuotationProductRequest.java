package com.itradingsolutions.itex.api.ip.q.models.requests;

import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationProductCondition;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * {@code profitMargin} is a direct percentage (10.00 = 10%), not a fraction.
 */
public record IpQuotationProductRequest(

        @NotNull(message = "Quotation Quote Request reference is required")
        UUID quotationsQuoteRequestId,

        @NotNull(message = "Product is required")
        UUID quoteRequestProductId,

        @NotNull(message = "Profit margin is required")
        @DecimalMin(value = "0.01", message = "Profit margin must be at least 0.01")
        @DecimalMax(value = "100", message = "Profit margin cannot exceed 100")
        BigDecimal profitMargin,

        @NotNull(message = "Condition is required")
        IpQuotationProductCondition condition
) {
}
