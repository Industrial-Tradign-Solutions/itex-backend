package com.itradingsolutions.itex.api.partners.clients.models.responses;

import com.itradingsolutions.itex.api.masters.industry.models.responses.BasicIndustryResponse;
import com.itradingsolutions.itex.api.partners.clients.models.enums.ClientStatus;


import java.util.UUID;

public record ListClientResponse(
        UUID id,
        String code,
        String name,
        String taxId,
        String city,
        String address,
        BasicIndustryResponse industry,
        ClientStatus status
) {
}
