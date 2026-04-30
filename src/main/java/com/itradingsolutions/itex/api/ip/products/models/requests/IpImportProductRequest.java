package com.itradingsolutions.itex.api.ip.products.models.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IpImportProductRequest extends  IpProductRequest {
    @NotNull(message = "Valid Import is required")
    private boolean validImport;
    @NotNull(message = "Save Brand is required")
    private boolean saveBrand;
    @NotNull(message = "Save Coo is required")
    private boolean saveCoo;
}
