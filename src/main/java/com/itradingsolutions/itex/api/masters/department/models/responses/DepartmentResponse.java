package com.itradingsolutions.itex.api.masters.department.models.responses;

import com.itradingsolutions.itex.api.masters.common.models.responses.BaseMasterWithDescriptionResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class DepartmentResponse extends BaseMasterWithDescriptionResponse {

        private boolean clientInfo;
        private boolean supplierInfo;
}
