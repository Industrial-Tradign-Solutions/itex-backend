package com.itradingsolutions.itex.api.admin.role.models.dto.ids;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
public class RoleMenuIdDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -2153354066384956418L;

    private UUID roleId;
    private Long menuId;
}
