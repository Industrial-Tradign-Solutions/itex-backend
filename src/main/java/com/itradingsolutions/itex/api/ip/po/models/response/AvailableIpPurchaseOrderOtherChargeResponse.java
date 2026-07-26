package com.itradingsolutions.itex.api.ip.po.models.response;

import java.math.BigDecimal;
import java.util.UUID;

public record AvailableIpPurchaseOrderOtherChargeResponse(
        UUID id,
        BigDecimal value,
        String description,
        String sourceNumber
) {
}
