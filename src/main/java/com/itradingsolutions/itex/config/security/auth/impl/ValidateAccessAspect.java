package com.itradingsolutions.itex.config.security.auth.impl;

import com.itradingsolutions.itex.api.admin.role.exceptions.ActionNotAccessException;
import com.itradingsolutions.itex.api.admin.role.exceptions.ModuleNotAccessException;
import com.itradingsolutions.itex.api.admin.role.models.dto.RoleActionDTO;
import com.itradingsolutions.itex.api.admin.role.models.dto.RoleMenuDTO;
import com.itradingsolutions.itex.api.admin.user.models.dto.UserDTO;
import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleOption;
import com.itradingsolutions.itex.api.admin.role.services.IRoleService;
import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import com.itradingsolutions.itex.api.common.util.exceptions.SystemOutException;
import com.itradingsolutions.itex.config.security.auth.AccessToAction;
import com.itradingsolutions.itex.config.security.auth.AccessToModule;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Aspect
@Component
@RequiredArgsConstructor
public class ValidateAccessAspect {
    private final IUserService userService;

    @Before("@annotation(validAccessToActionAnnotation)")
    public void  beforeAccessToActionAnnotation(AccessToAction validAccessToActionAnnotation) {
        validateDisableTime();
        ModuleAction action = validAccessToActionAnnotation.action();
        UserDTO user = userService.getUserAuthenticatedDto();

        if (!user.getRole().getId().equals(IRoleService.SUPER_ADMIN_ID)) {
            validAccessToModule(validAccessToActionAnnotation.action().getModuleOption(), user);
            validateAccessToAction(action, user);
        }
    }

    @Before(("@annotation(validateAccessToModuleAnnotation)"))
    public void beforeAccessToModuleAnnotation(AccessToModule validateAccessToModuleAnnotation) {
        validateDisableTime();
        UserDTO user = userService.getUserAuthenticatedDto();
        if (!user.getRole().getId().equals(IRoleService.SUPER_ADMIN_ID))
            validAccessToModule(validateAccessToModuleAnnotation.option(), user);
    }

    private void validateAccessToAction(ModuleAction moduleAction, UserDTO user) {
        if (user.getRole().getActions() != null && !user.getRole().getActions().isEmpty()) {
            for (RoleActionDTO action : user.getRole().getActions()) {
                if (action.getAction().getId().equals(moduleAction.getId()))
                    return;
            }
        }
        throw new ActionNotAccessException("The user " + user.getEmail() + " does not have access to the action " + moduleAction.getName());
    }
    private void validAccessToModule(ModuleOption menuOption, UserDTO user) {
        if (user.getRole().getMenus() != null && !user.getRole().getMenus().isEmpty()) {
            for (RoleMenuDTO menu: user.getRole().getMenus()) {
                if (menu.getMenu().getId().equals(menuOption.getId()) && menu.getMenu().isActive())
                    return;
            }
        }
        throw new ModuleNotAccessException("The user " + user.getEmail() + " does not have access to the module " + menuOption.getOptionName());
    }

    public void validateDisableTime() {
        LocalTime now = LocalTime.now();
        LocalTime start = LocalTime.of(23, 50);
        LocalTime end = LocalTime.of(23, 59);
        if (!now.isBefore(start) && !now.isAfter(end))
            throw new SystemOutException("The system is in a reboot, it will be enabled after 12:00AM.");
    }

}
