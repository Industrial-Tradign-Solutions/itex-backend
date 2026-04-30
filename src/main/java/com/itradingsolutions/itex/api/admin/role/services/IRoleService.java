package com.itradingsolutions.itex.api.admin.role.services;

import com.itradingsolutions.itex.api.admin.role.models.dto.RoleDTO;
import com.itradingsolutions.itex.api.admin.role.models.entities.RoleEntity;
import com.itradingsolutions.itex.api.common.service.CommonService;

import java.util.UUID;

public interface IRoleService extends CommonService<RoleDTO> {

    UUID SUPER_ADMIN_ID = UUID.fromString("1b5c43fd-d711-4557-b2dd-f29a00415ee6");
    RoleEntity findEntityById(UUID id, boolean validateActive);

}
