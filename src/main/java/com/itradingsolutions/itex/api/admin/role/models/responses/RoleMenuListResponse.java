package com.itradingsolutions.itex.api.admin.role.models.responses;

import com.itradingsolutions.itex.api.admin.role.models.dto.MenuItemDTO;

import java.util.List;

public record RoleMenuListResponse(
        List<MenuItemDTO> unassignedMenus,
        List<MenuItemDTO> assignedMenus
) {}
