package com.itradingsolutions.itex.api.masters.brand.models.responses;

import java.time.ZonedDateTime;
import java.util.UUID;

public record BrandResponse(
        UUID id,
        String name,
        Boolean active,
        ZonedDateTime createdAt
) {

}