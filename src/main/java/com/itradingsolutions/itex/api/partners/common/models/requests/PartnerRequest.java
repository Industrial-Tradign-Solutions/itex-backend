package com.itradingsolutions.itex.api.partners.common.models.requests;

import com.itradingsolutions.itex.api.common.util.models.enums.Language;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentMethod;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public abstract class PartnerRequest<S, I extends PartnerInfoDepRequest<?>> {
    @NotBlank(message = "The name field cannot be empty")
    private String name;

    private String taxId;

    @NotNull(message = "The Status field is required")
    private S status;

    @NotNull(message = "The language field is required")
    private Language language;

    @NotNull(message = "The Payment Method field is required")
    private PaymentMethod paymentMethod;

    private PaymentTerms paymentTerms;

    private String address;

    private UUID cityId;

    private String zipCode;

    private String notes;

    private String webSite;

    private String countryCode;

    private String cityCode;

    private String phoneNumber;

    @Valid
    @Size(min = 1, message = "Minimum one information field per department")
    private List<@Valid I> infoByDepartment;
}
