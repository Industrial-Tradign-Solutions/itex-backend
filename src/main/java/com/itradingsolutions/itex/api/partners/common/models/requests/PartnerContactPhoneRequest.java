package com.itradingsolutions.itex.api.partners.common.models.requests;

import com.itradingsolutions.itex.api.common.util.models.enums.PhoneType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public abstract class PartnerContactPhoneRequest {

    private UUID id;

    @NotNull(message = "The type code field is required")
    private PhoneType type;

    private String countryCode;
    private String cityCode;

    private String phoneNumber;

    private String ext;

    @NotNull(message = "The Is Main field is required")
    private boolean validMain;
}
