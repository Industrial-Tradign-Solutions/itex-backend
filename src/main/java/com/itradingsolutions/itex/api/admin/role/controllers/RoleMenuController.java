package com.itradingsolutions.itex.api.admin.role.controllers;

import com.itradingsolutions.itex.api.admin.role.models.dto.MainMenuDTO;
import com.itradingsolutions.itex.api.admin.role.models.dto.RoleMenuDTO;
import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleOption;
import com.itradingsolutions.itex.api.admin.role.models.requests.RoleMenuRequest;
import com.itradingsolutions.itex.api.admin.role.models.responses.RoleMenuIdsResponse;
import com.itradingsolutions.itex.api.admin.role.models.responses.RoleMenuListResponse;
import com.itradingsolutions.itex.api.admin.role.services.IRoleMenuService;
import com.itradingsolutions.itex.api.common.controller.CommonController;
import com.itradingsolutions.itex.api.common.util.models.enums.HistoryActions;
import com.itradingsolutions.itex.config.security.auth.AccessToAction;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/roles/menu")
@RequiredArgsConstructor
public class RoleMenuController extends CommonController {

    private final IRoleMenuService roleMenuService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<RoleMenuListResponse> listAllRoleMenus(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(roleMenuService.findAllMenusByRole(id));
    }

    @GetMapping("/list-id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<RoleMenuIdsResponse> listAllRoleMenuIds(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(roleMenuService.findAllMenuIds(id));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_ROLE_MENUS)
    public ResponseEntity<List<RoleMenuDTO>> updateRoleMenu(
            @RequestBody RoleMenuRequest roleMenuRequest,
            @PathVariable UUID id
    ) {

        var oldRoleModules = roleMenuService.findAllMenuIds(id);
        var resp = roleMenuService.updateRoleMenus(id, roleMenuRequest);
        var newRoleModules = roleMenuService.findAllMenuIds(id);

        historyService.saveHistory(HistoryActions.UPDATE_ROLES_MODULES, ModuleOption.ROLES_AND_PERMISSIONS, id, oldRoleModules, newRoleModules);
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

    @GetMapping("/list-menus/{roleId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<MainMenuDTO>> listMenuByRoleId(
            @PathVariable UUID roleId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(roleMenuService.listMenuByRoleId(roleId));
    }
}
