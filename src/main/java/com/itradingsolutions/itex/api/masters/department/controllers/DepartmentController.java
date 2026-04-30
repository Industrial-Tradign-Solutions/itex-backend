package com.itradingsolutions.itex.api.masters.department.controllers;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleOption;
import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.masters.common.controller.CommonMasterController;
import com.itradingsolutions.itex.api.masters.department.models.dto.DepartmentDTO;
import com.itradingsolutions.itex.api.masters.department.models.mappers.DepartmentMapper;
import com.itradingsolutions.itex.api.masters.department.models.requests.DepartmentRequest;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import com.itradingsolutions.itex.api.masters.department.models.responses.BasicDepartmentResponse;
import com.itradingsolutions.itex.api.masters.department.models.responses.DepartmentResponse;
import com.itradingsolutions.itex.api.masters.department.models.responses.ListsDepartmentsResponse;
import com.itradingsolutions.itex.api.masters.department.services.IDepartmentService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/master/departments")
@Validated
@RequiredArgsConstructor
public class DepartmentController extends CommonMasterController<
        IDepartmentService,
        DepartmentDTO,
        DepartmentMapper,
        BasicDepartmentResponse,
        ListsDepartmentsResponse,
        DepartmentResponse,
        DepartmentRequest> {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.DEPARTMENTS)
    public ResponseEntity<List<DepartmentResponse>> listAllDepartments() {
        return super.listAll();
    }

    @GetMapping("/show_info")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<BasicDepartmentResponse>> listBasicDepartments(
            @RequestParam(name = "clientInfo", defaultValue = "false") boolean isShowOnlyClientInfo,
            @RequestParam(name = "supplierInfo", defaultValue = "false") boolean isShowOnlySupplierInfo
    ) {
        var listDepartments = service.listAllShowInfo(isShowOnlyClientInfo, isShowOnlySupplierInfo);
        return ResponseEntity
                .ok(listDepartments.stream()
                        .map(mapper::dtoToResponseBasic)
                        .toList()
                );
    }

    @GetMapping("/basic")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ListsDepartmentsResponse> listsActiveDepartments() {
        return super.listBasics();
    }

    @DeleteMapping("/disable/{departmentId}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.DISABLE_DEPARTMENT)
    public ResponseEntity<MessageResponse<UUID>> disableDepartment(
            @PathVariable UUID departmentId
    ) {
        return super.disable(departmentId);
    }

    @PatchMapping("/enable/{departmentId}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.ENABLE_DEPARTMENT)
    public ResponseEntity<MessageResponse<UUID>> enableDepartment(
            @PathVariable UUID departmentId
    ) {
        return super.enable(departmentId);
    }

    @GetMapping("/{departmentId}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.DEPARTMENTS)
    public ResponseEntity<DepartmentResponse> findDepartmentById(
            @PathVariable UUID departmentId
    ) {
        return super.findById(departmentId);
    }


    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.CREATE_DEPARTMENT)
    public ResponseEntity<MessageResponse<DepartmentResponse>> createDepartment(
            @RequestBody @Valid DepartmentRequest departmentRequest
    ) {
        return super.create(departmentRequest);
    }

    @PutMapping("/update/{departmentId}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_DEPARTMENT)
    public ResponseEntity<MessageResponse<DepartmentResponse>> updateDepartment(
            @PathVariable UUID departmentId,
            @RequestBody @Valid DepartmentRequest departmentRequest
    ) {
        return super.update(departmentId, departmentRequest);
    }

    @Override
    public ListsDepartmentsResponse createListsResponse(
            List<BasicDepartmentResponse> enables,
            List<BasicDepartmentResponse> disables) {
        return ListsDepartmentsResponse
                .builder()
                .disables(disables)
                .enables(enables)
                .build();
    }

    @Override
    public WebSocketMessageValue getWebSocketMessageValue() {
        return WebSocketMessageValue.LIST_DEPARTMENTS;
    }

    @Override
    public ModuleOption getModuleOption() {
        return ModuleOption.DEPARTMENTS;
    }
}
