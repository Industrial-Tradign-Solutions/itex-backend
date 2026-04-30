package com.itradingsolutions.itex.api.ip.products.models.requests;

import com.itradingsolutions.itex.api.common.util.models.enums.FreightClass;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class IpProductRequest {

    private UUID brandId;

    @NotEmpty(message = "Description is required")
    private String description;

    private String clientDescription;

    private String mfrReference;

    private String clientReference;

    private String netWeightLbs;

    private Integer nmfc;

    private FreightClass freightClass;

    private String notes;

    private String keywords;

    private Integer htsScheduleBNumber;

    private String eccn;

    private UUID cooId;

    @NotNull(message = "Is Battery is required")
    private boolean battery;

    @NotNull(message = "Is Hazmat is required")
    private boolean hazmat;

    @NotNull(message = "Dual Use is required")
    private boolean dualUse;
}
