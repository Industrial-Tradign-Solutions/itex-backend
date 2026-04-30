package com.itradingsolutions.itex.api.ip.products.models.responses;

import java.util.UUID;

public record BasicIpProductResponse(
        UUID id,
        String name,
        String description,
        String clientDescription,
        String mfrReference,
        String clientReference
) {
}
