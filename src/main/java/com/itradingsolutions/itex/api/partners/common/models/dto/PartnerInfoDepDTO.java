package com.itradingsolutions.itex.api.partners.common.models.dto;

import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;
import com.itradingsolutions.itex.api.masters.department.models.dto.DepartmentDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public abstract class PartnerInfoDepDTO<C extends PartnerContactDTO<?>> extends BaseDTO {

    private DepartmentDTO department;
    private String notes;
    private List<C> listContacts;

}
