package com.itradingsolutions.itex.api.admin.user.models.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class UserDepartmentIdDTO {

    private UUID idUser;
    private UUID idDepartment;
}
