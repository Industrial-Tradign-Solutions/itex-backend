package com.itradingsolutions.itex.api.partners.common.models.dto;

import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;
import com.itradingsolutions.itex.api.common.util.models.enums.PhoneType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class PartnerContactPhoneDTO extends BaseDTO {

    private PhoneType type;
    private String countryCode;
    private String cityCode;
    private String phoneNumber;
    private String ext;
    private boolean validMain;
    private String fullPhone;
}
