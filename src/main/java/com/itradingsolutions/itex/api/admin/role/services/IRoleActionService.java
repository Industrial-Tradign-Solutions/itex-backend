package com.itradingsolutions.itex.api.admin.role.services;

import com.itradingsolutions.itex.api.admin.role.models.dto.RoleActionDTO;
import com.itradingsolutions.itex.api.admin.role.models.requests.RoleActionRequest;
import com.itradingsolutions.itex.api.admin.role.models.responses.RoleActionIdsResponse;
import com.itradingsolutions.itex.api.admin.role.models.responses.RoleActionListResponse;

import java.util.List;
import java.util.UUID;

public interface IRoleActionService {

    RoleActionListResponse findAllActionsByRole(UUID roleId);
    RoleActionIdsResponse findAllActionIds(UUID roleId);
    List<RoleActionDTO> updateRoleActions(UUID idRole, RoleActionRequest roleActionRequest);

    //Methods to call in role menu service
    void deleteActionsWhenMenuDelete(UUID roleId, List<Long> roleMenuIds);
    void deleteActionsWithMenu(UUID roleId);
}
