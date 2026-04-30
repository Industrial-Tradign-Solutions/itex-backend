package com.itradingsolutions.itex.api.admin.user.models.dto;

import com.itradingsolutions.itex.api.masters.department.models.dto.DepartmentDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserDepartmentDTO {
    private UserDepartmentIdDTO id;
    private DepartmentDTO department;
}
