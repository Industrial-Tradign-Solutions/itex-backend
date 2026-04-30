package com.itradingsolutions.itex.api.admin.role.services;

import com.itradingsolutions.itex.api.admin.role.models.dto.MainMenuDTO;
import com.itradingsolutions.itex.api.admin.role.models.dto.RoleMenuDTO;
import com.itradingsolutions.itex.api.admin.role.models.requests.RoleMenuRequest;
import com.itradingsolutions.itex.api.admin.role.models.responses.RoleMenuIdsResponse;
import com.itradingsolutions.itex.api.admin.role.models.responses.RoleMenuListResponse;

import java.util.List;
import java.util.UUID;

public interface IRoleMenuService {

    List<MainMenuDTO> listMenuByRoleId(UUID roleId);
    RoleMenuListResponse findAllMenusByRole(UUID roleId);
    RoleMenuIdsResponse findAllMenuIds(UUID roleId);
    List<RoleMenuDTO> updateRoleMenus(UUID idRole, RoleMenuRequest roleMenuRequest);
}
