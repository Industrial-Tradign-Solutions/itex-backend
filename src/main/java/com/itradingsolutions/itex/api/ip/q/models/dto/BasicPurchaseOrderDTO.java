package com.itradingsolutions.itex.api.ip.q.models.dto;

import java.util.UUID;

public record BasicPurchaseOrderDTO(
        UUID id,
        String number
) {
}
