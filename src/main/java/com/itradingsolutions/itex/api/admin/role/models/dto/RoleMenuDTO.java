package com.itradingsolutions.itex.api.admin.role.models.dto;

import com.itradingsolutions.itex.api.admin.role.models.dto.ids.RoleMenuIdDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
public class RoleMenuDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2930945800049315210L;

    private RoleMenuIdDTO id;
    private MenuItemDTO menu;
}
