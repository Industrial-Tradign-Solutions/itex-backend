package com.itradingsolutions.itex.api.ip.q.models.response;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Response object representing an Other Charge in a Quotation.
 * <p>
 * Contains the ID, value, and description of the charge.
 * </p>
 *
 * @param id unique identifier of the other charge
 * @param value monetary value of the charge
 * @param description brief description of the charge
 */
public record IpQuotationOtherChargeResponse(
        UUID id,
        BigDecimal value,
        String description
) {
}
