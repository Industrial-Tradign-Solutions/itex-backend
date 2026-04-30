package com.itradingsolutions.itex.api.partners.common.models.dto;

import com.itradingsolutions.itex.api.common.models.dto.BaseAuditFieldsDTO;
import com.itradingsolutions.itex.api.common.util.models.enums.Language;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentMethod;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;
import com.itradingsolutions.itex.api.masters.location.models.dto.CityDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public abstract class PartnerDTO<S, I extends PartnerInfoDepDTO<?>> extends BaseAuditFieldsDTO {

    private String name;
    private String taxId;
    private S status;
    private Language language;
    private PaymentMethod paymentMethod;
    private PaymentTerms paymentTerms;
    private String address;
    private CityDTO city;
    private String zipCode;
    private String notes;
    private String webSite;
    private String countryCode;
    private String cityCode;
    private String phoneNumber;
    private List<I> infoByDepartment;
}
