package com.itradingsolutions.itex.api.admin.role.models.mappers;

import com.itradingsolutions.itex.api.admin.role.models.dto.MainMenuDTO;
import com.itradingsolutions.itex.api.admin.role.models.dto.MenuItemDTO;
import com.itradingsolutions.itex.api.admin.role.models.entities.MenuEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MenuMapper {
    MainMenuDTO menuEntityToMainMenu(MenuEntity entity);
    MenuItemDTO menuEntityToMenuItem(MenuEntity entity);
}
