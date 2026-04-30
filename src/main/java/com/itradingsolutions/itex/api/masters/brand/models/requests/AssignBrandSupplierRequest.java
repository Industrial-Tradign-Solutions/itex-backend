package com.itradingsolutions.itex.api.masters.brand.models.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class AssignBrandSupplierRequest {

    @NotNull(message = "{master.brand.assign.brandId}")
    private UUID brandId;

    @NotNull(message = "{master.brand.assign.supplierId}")
    private UUID supplierId;
}
