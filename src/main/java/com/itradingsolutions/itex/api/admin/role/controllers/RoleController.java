package com.itradingsolutions.itex.api.admin.role.controllers;

import com.itradingsolutions.itex.api.admin.role.models.dto.RoleDTO;
import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleOption;
import com.itradingsolutions.itex.api.admin.role.models.responses.BasicRoleResponse;
import com.itradingsolutions.itex.api.admin.role.models.responses.ListsRoleResponse;
import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.admin.role.models.mappers.RoleMapper;
import com.itradingsolutions.itex.api.admin.role.models.requests.RoleRequest;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import com.itradingsolutions.itex.api.admin.role.models.responses.RoleResponse;
import com.itradingsolutions.itex.api.admin.role.services.IRoleService;
import com.itradingsolutions.itex.api.masters.common.controller.CommonMasterController;
import com.itradingsolutions.itex.config.security.auth.AccessToAction;
import com.itradingsolutions.itex.config.security.auth.AccessToModule;
import com.itradingsolutions.itex.config.websocket.WebSocketMessageValue;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/roles")
@Validated

@RequiredArgsConstructor
public class RoleController extends CommonMasterController<
        IRoleService,
        RoleDTO,
        RoleMapper,
        BasicRoleResponse,
        ListsRoleResponse,
        RoleResponse,
        RoleRequest> {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.ROLES_AND_PERMISSIONS)
    public ResponseEntity<List<RoleResponse>> listAllRoles() {
        return super.listAll();
    }

    @GetMapping("/basic")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ListsRoleResponse> listAllBasicRoles() {
        return super.listBasics();
    }

    @GetMapping("/{roleId}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.ROLES_AND_PERMISSIONS)
    public ResponseEntity<RoleResponse> findRoleById(
            @PathVariable UUID roleId
    ) {
        return super.findById(roleId);
    }

    @DeleteMapping("/disable/{roleId}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.DISABLE_ROLE)
    public ResponseEntity<MessageResponse<UUID>> disableRole(
            @PathVariable UUID roleId
    ) {
        return super.disable(roleId);
    }

    @PatchMapping("/enable/{roleId}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.ENABLE_ROLE)
    public ResponseEntity<MessageResponse<UUID>> enableRole(
            @PathVariable UUID roleId
    ) {
        return super.enable(roleId);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.CREATE_ROLE)
    public ResponseEntity<MessageResponse<RoleResponse>> createRole(
            @RequestBody @Valid RoleRequest roleRequest
    ) {
        return super.create(roleRequest);
    }

    @PutMapping("/update/{roleId}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_ROLE)
    public ResponseEntity<MessageResponse<RoleResponse>> updateRole(
            @PathVariable UUID roleId,
            @RequestBody @Valid RoleRequest roleRequest
    ) {
        return super.update(roleId, roleRequest);
    }

    @Override
    public ListsRoleResponse createListsResponse(List<BasicRoleResponse> enables, List<BasicRoleResponse> disables) {
        return ListsRoleResponse
                .builder()
                .disables(disables)
                .enables(enables)
                .build();
    }

    @Override
    public WebSocketMessageValue getWebSocketMessageValue() {
        return WebSocketMessageValue.LIST_ROLES;
    }

    @Override
    public ModuleOption getModuleOption() {
        return ModuleOption.ROLES_AND_PERMISSIONS;
    }
}
