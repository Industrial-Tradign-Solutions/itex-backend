package com.itradingsolutions.itex.api.masters.location.controllers;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleOption;
import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.masters.common.controller.CommonMasterController;
import com.itradingsolutions.itex.api.masters.location.models.dto.CityDTO;
import com.itradingsolutions.itex.api.masters.location.models.mappers.CityMapper;
import com.itradingsolutions.itex.api.masters.location.models.requests.CityRequest;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import com.itradingsolutions.itex.api.masters.location.models.responses.BasicCityResponse;
import com.itradingsolutions.itex.api.masters.location.models.responses.CityResponse;
import com.itradingsolutions.itex.api.masters.location.models.responses.ListsCitiesResponse;
import com.itradingsolutions.itex.api.masters.location.services.ICityService;
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
@RequestMapping("/master/locations/cities")
@Validated
@RequiredArgsConstructor
public class CityController extends CommonMasterController<
        ICityService,
        CityDTO,
        CityMapper,
        BasicCityResponse,
        ListsCitiesResponse,
        CityResponse,
        CityRequest> {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.LOCATIONS)
    public ResponseEntity<List<CityResponse>> listAllCities() {
        return super.listAll();
    }

    @GetMapping("/basic")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ListsCitiesResponse> listAllBasicsCities() {
        return super.listBasics();
    }

    @GetMapping("/{cityId}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.LOCATIONS)
    public ResponseEntity<CityResponse> findCityById(
            @PathVariable UUID cityId
    ) {
        return super.findById(cityId);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.CREATE_CITY)
    public ResponseEntity<MessageResponse<CityResponse>> createCity(
            @RequestBody @Valid CityRequest cityRequest
    ) {
        return super.create(cityRequest);
    }

    @PutMapping("/update/{cityId}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_CITY)
    public ResponseEntity<MessageResponse<CityResponse>> updateCity(
            @PathVariable UUID cityId,
            @RequestBody @Valid CityRequest cityRequest
    ) {
        return super.update(cityId, cityRequest);
    }

    @Override
    public ListsCitiesResponse createListsResponse(List<BasicCityResponse> enables, List<BasicCityResponse> disables) {
        return ListsCitiesResponse
                .builder()
                .enables(enables)
                .disables(disables)
                .build();
    }

    @Override
    public WebSocketMessageValue getWebSocketMessageValue() {
        return WebSocketMessageValue.LIST_CITIES;
    }

    @Override
    public ModuleOption getModuleOption() {
        return ModuleOption.LOCATIONS;
    }

}
