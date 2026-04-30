package com.itradingsolutions.itex.api.admin.role.models.mappers;

import com.itradingsolutions.itex.api.admin.role.models.dto.MenuItemDTO;
import com.itradingsolutions.itex.api.admin.role.models.dto.RoleMenuDTO;
import com.itradingsolutions.itex.api.admin.role.models.entities.MenuEntity;
import com.itradingsolutions.itex.api.admin.role.models.entities.RoleMenuEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMenuMapper {
    List<MenuItemDTO> menuEntitiesToMenuItems(List<MenuEntity> roleMenuEntities);
    MenuItemDTO menuEntityToMenuItem(MenuEntity menuEntity);
    List<RoleMenuDTO> roleMenuEntitiesToRoleMenus(List<RoleMenuEntity> roleMenuEntities);
}
