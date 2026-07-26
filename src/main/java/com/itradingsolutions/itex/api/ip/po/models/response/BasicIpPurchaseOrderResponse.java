package com.itradingsolutions.itex.api.ip.po.models.response;

import java.util.UUID;

public record BasicIpPurchaseOrderResponse(
        UUID id,
        String number
) {
}
