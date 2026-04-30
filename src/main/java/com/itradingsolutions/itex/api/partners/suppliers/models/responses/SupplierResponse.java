package com.itradingsolutions.itex.api.partners.suppliers.models.responses;

import com.itradingsolutions.itex.api.admin.user.models.responses.BasicUserResponse;
import com.itradingsolutions.itex.api.common.util.models.enums.Language;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentMethod;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;
import com.itradingsolutions.itex.api.masters.location.models.responses.BasicCityResponse;
import com.itradingsolutions.itex.api.partners.suppliers.models.enums.SupplierStatus;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record SupplierResponse(
        UUID id,
        String name,
        String taxId,
        SupplierStatus status,
        Language language,
        PaymentMethod paymentMethod,
        PaymentTerms paymentTerms,
        String address,
        BasicCityResponse city,
        String zipCode,
        String notes,
        String wireAchInstructions,
        String webSite,
        String countryCode,
        String cityCode,
        String phoneNumber,
        List<SupplierInfoDepResponse> infoByDepartment,
        BasicUserResponse createdBy,
        ZonedDateTime createdAt,
        BasicUserResponse updatedBy,
        ZonedDateTime updatedAt,
        BasicUserResponse openBy,
        ZonedDateTime openAt
) {
}
