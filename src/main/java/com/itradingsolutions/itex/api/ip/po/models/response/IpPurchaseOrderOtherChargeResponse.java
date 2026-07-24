package com.itradingsolutions.itex.api.ip.po.models.response;

import java.math.BigDecimal;
import java.util.UUID;

public record IpPurchaseOrderOtherChargeResponse(
        UUID id,
        BigDecimal value,
        String description
) {
}