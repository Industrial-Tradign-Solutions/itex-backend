package com.itradingsolutions.itex.api.admin.role.controllers;

import com.itradingsolutions.itex.api.admin.role.models.dto.RoleActionDTO;
import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleOption;
import com.itradingsolutions.itex.api.admin.role.models.requests.RoleActionRequest;
import com.itradingsolutions.itex.api.admin.role.models.responses.RoleActionIdsResponse;
import com.itradingsolutions.itex.api.admin.role.models.responses.RoleActionListResponse;
import com.itradingsolutions.itex.api.admin.role.services.IRoleActionService;
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
@RequestMapping("/admin/roles/action")
@RequiredArgsConstructor
public class RoleActionController extends CommonController {

    private final IRoleActionService roleActionService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<RoleActionListResponse> listAllRoleActions(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(roleActionService.findAllActionsByRole(id));
    }

    @GetMapping("/list-id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<RoleActionIdsResponse> listAllRoleActionIds(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(roleActionService.findAllActionIds(id));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_ROLE_ACTIONS)
    public ResponseEntity<List<RoleActionDTO>> updateRoleAction(
            @RequestBody RoleActionRequest roleActionRequest,
            @PathVariable UUID id
    ) {
        var oldRoleActions = roleActionService.findAllActionIds(id);
        var resp = roleActionService.updateRoleActions(id, roleActionRequest);
        var newRoleActions = roleActionService.findAllActionIds(id);
        historyService.saveHistory(HistoryActions.UPDATE_ROLES_ACTIONS, ModuleOption.ROLES_AND_PERMISSIONS, id, oldRoleActions, newRoleActions);
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }

}
