package com.itradingsolutions.itex.api.masters.department.models.requests;

import com.itradingsolutions.itex.api.masters.common.models.requests.BaseMasterWithDescriptionRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentRequest extends BaseMasterWithDescriptionRequest {

    @NotNull(message = "Client info is required")
    private boolean clientInfo;

    @NotNull(message = "Supplier info is required")
    private boolean supplierInfo;
}
