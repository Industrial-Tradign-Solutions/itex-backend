package com.itradingsolutions.itex.api.partners.suppliers.models.responses;

import com.itradingsolutions.itex.api.common.util.models.enums.PhoneType;

import java.util.UUID;

public record SupplierContactPhoneResponse(
        UUID id,
        PhoneType type,
        String countryCode,
        String cityCode,
        String phoneNumber,
        String ext,
        boolean validMain
) {
}
