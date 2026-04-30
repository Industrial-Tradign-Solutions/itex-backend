package com.itradingsolutions.itex.api.masters.department.models.dto;

import com.itradingsolutions.itex.api.masters.common.models.dto.BaseMasterWithDescriptionDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DepartmentDTO extends BaseMasterWithDescriptionDTO {

    private boolean clientInfo;
    private boolean supplierInfo;
}
