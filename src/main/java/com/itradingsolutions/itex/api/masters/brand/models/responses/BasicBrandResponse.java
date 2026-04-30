package com.itradingsolutions.itex.api.masters.brand.models.responses;



import java.util.UUID;

public record BasicBrandResponse(
        UUID id,
        String name,
        boolean active
) {
}
