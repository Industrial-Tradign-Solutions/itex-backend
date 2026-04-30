package com.itradingsolutions.itex.api.admin.role.models.dto;

import com.itradingsolutions.itex.api.masters.common.models.dto.BaseMasterWithDescriptionDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class RoleDTO extends BaseMasterWithDescriptionDTO {

    private List<RoleMenuDTO> menus;
    private List<RoleActionDTO> actions;
}
