package com.itradingsolutions.itex.api.masters.location.controllers;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleOption;
import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.masters.common.controller.CommonMasterController;
import com.itradingsolutions.itex.api.masters.location.models.dto.StateDTO;
import com.itradingsolutions.itex.api.masters.location.models.mappers.StateMapper;
import com.itradingsolutions.itex.api.masters.location.models.requests.StateRequest;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import com.itradingsolutions.itex.api.masters.location.models.responses.BasicStateResponse;
import com.itradingsolutions.itex.api.masters.location.models.responses.ListsStatesResponse;
import com.itradingsolutions.itex.api.masters.location.models.responses.StateResponse;
import com.itradingsolutions.itex.api.masters.location.services.IStateService;
import com.itradingsolutions.itex.config.security.auth.AccessToAction;
import com.itradingsolutions.itex.config.security.auth.AccessToModule;
import com.itradingsolutions.itex.config.websocket.WebSocketMessageValue;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/master/locations/states")
@Validated
@RequiredArgsConstructor
public class StateController extends CommonMasterController<
        IStateService,
        StateDTO,
        StateMapper,
        BasicStateResponse,
        ListsStatesResponse,
        StateResponse,
        StateRequest> {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.LOCATIONS)
    public ResponseEntity<List<StateResponse>> listAllStates() {
        return super.listAll();
    }

    @GetMapping("/basic")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ListsStatesResponse> listAllBasicsStates() {
        return super.listBasics();
    }

    @GetMapping("/{stateId}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.LOCATIONS)
    public ResponseEntity<StateResponse> findStateById(
            @PathVariable UUID stateId
    ) {
        return super.findById(stateId);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.CREATE_STATE)
    public ResponseEntity<MessageResponse<StateResponse>> createState(
            @RequestBody @Valid StateRequest stateRequest
    ) {
        return super.create(stateRequest);
    }

    @PutMapping("/update/{stateId}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_STATE)
    public ResponseEntity<MessageResponse<StateResponse>> updateState(
            @PathVariable UUID stateId,
            @RequestBody @Valid StateRequest stateRequest
    ) {
        return super.update(stateId, stateRequest);
    }

    @Override
    public ListsStatesResponse createListsResponse(List<BasicStateResponse> enables, List<BasicStateResponse> disables) {
        return ListsStatesResponse
                .builder()
                .enables(enables)
                .disables(disables)
                .build();
    }

    @Override
    public WebSocketMessageValue getWebSocketMessageValue() {
        return WebSocketMessageValue.LIST_STATES;
    }

    @Override
    public ModuleOption getModuleOption() {
        return ModuleOption.LOCATIONS;
    }
}
