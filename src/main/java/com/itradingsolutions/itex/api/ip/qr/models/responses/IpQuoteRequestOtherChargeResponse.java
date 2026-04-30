package com.itradingsolutions.itex.api.ip.qr.models.responses;

import java.math.BigDecimal;
import java.util.UUID;

public record IpQuoteRequestOtherChargeResponse(
        UUID id,
        BigDecimal value,
        String description
) {
}
