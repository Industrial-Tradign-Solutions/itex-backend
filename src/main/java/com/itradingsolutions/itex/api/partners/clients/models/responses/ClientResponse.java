package com.itradingsolutions.itex.api.partners.clients.models.responses;

import com.itradingsolutions.itex.api.admin.user.models.responses.BasicUserResponse;
import com.itradingsolutions.itex.api.common.util.models.enums.Language;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentMethod;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;
import com.itradingsolutions.itex.api.masters.industry.models.responses.BasicIndustryResponse;
import com.itradingsolutions.itex.api.masters.location.models.responses.BasicCityResponse;
import com.itradingsolutions.itex.api.partners.clients.models.enums.ClientStatus;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record ClientResponse(
        UUID id,
        String code,
        String name,
        String taxId,
        ClientStatus status,
        Language language,
        PaymentMethod paymentMethod,
        PaymentTerms paymentTerms,
        String address,
        BasicCityResponse city,
        String zipCode,
        String notes,
        BasicIndustryResponse industry,
        String webSite,
        String countryCode,
        String cityCode,
        String phoneNumber,
        List<ClientInfoDepResponse> infoByDepartment,
        BasicUserResponse createdBy,
        ZonedDateTime createdAt,
        BasicUserResponse updatedBy,
        ZonedDateTime updatedAt,
        BasicUserResponse openBy,
        ZonedDateTime openAt,
        BasicUserResponse changeProspectToClientBy,
        ZonedDateTime changeProspectToClientAt
) {
}
