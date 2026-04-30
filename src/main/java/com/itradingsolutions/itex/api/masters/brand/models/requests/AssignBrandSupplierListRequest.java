package com.itradingsolutions.itex.api.masters.brand.models.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class AssignBrandSupplierListRequest {
    @NotNull(message = "{master.brand.assign.supplierId}")
    private UUID supplierId;

    @NotNull(message = "{master.brand.assign.brandId}")
    @Size(min = 1, message = "{master.brand.assign.brandId}")
    private List<UUID> brandIds;
}
