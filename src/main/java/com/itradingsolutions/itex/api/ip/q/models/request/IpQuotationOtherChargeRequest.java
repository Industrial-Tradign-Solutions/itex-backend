package com.itradingsolutions.itex.api.ip.q.models.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Request object for creating or updating an Other Charge in a Quotation.
 * <p>
 * Validates that the description is not blank and the value is a non-negative number.
 * </p>
 *
 * @param description a brief description of the charge (e.g., "Freight", "Handling")
 * @param value the monetary value of the charge (must be >= 0)
 */
public record IpQuotationOtherChargeRequest(
        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Value is required")
        @Min(0)
        BigDecimal value
) {
}
