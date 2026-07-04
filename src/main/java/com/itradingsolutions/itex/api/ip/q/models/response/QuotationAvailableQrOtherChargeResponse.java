package com.itradingsolutions.itex.api.ip.q.models.response;

import java.math.BigDecimal;
import java.util.UUID;

public record QuotationAvailableQrOtherChargeResponse(
        UUID id,
        BigDecimal value,
        String description,
        String qrNumber
) {
}
