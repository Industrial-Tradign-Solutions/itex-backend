package com.itradingsolutions.itex.api.common.models.dto;

import com.itradingsolutions.itex.api.admin.user.models.dto.UserDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public abstract class BaseAuditFieldsDTO extends BaseDTO {

    private UserDTO createdBy;
    private UserDTO openBy;
    private UserDTO updatedBy;

    private ZonedDateTime openAt;
    private ZonedDateTime updatedAt;
}
