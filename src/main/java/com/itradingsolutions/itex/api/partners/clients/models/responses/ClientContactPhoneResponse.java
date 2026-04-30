package com.itradingsolutions.itex.api.partners.clients.models.responses;

import com.itradingsolutions.itex.api.common.util.models.enums.PhoneType;

import java.util.UUID;

public record ClientContactPhoneResponse(
        UUID id,
        PhoneType type,
        String countryCode,
        String cityCode,
        String phoneNumber,
        String ext,
        boolean validMain
) {
}
