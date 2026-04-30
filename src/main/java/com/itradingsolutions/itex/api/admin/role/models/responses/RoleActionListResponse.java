package com.itradingsolutions.itex.api.admin.role.models.responses;

import com.itradingsolutions.itex.api.admin.role.models.dto.ActionDTO;

import java.util.List;

public record RoleActionListResponse(
        List<ActionDTO> unassignedActions,
        List<ActionDTO> assignedActions
) {}
