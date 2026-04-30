package com.itradingsolutions.itex.api.masters.location.controllers;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleOption;
import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.masters.common.controller.CommonMasterController;
import com.itradingsolutions.itex.api.masters.location.models.dto.CountryDTO;
import com.itradingsolutions.itex.api.masters.location.models.mappers.CountryMapper;
import com.itradingsolutions.itex.api.masters.location.models.requests.CountryRequest;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import com.itradingsolutions.itex.api.masters.location.models.responses.BasicCountryResponse;
import com.itradingsolutions.itex.api.masters.location.models.responses.CountryResponse;
import com.itradingsolutions.itex.api.masters.location.models.responses.ListsCountriesResponse;
import com.itradingsolutions.itex.api.masters.location.services.ICountryService;
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
@RequestMapping("/master/locations/countries")
@Validated
@RequiredArgsConstructor
public class CountryController extends CommonMasterController<
        ICountryService,
        CountryDTO,
        CountryMapper,
        BasicCountryResponse,
        ListsCountriesResponse,
        CountryResponse,
        CountryRequest> {


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.LOCATIONS)
    public ResponseEntity<List<CountryResponse>> listAllCountries() {
        return super.listAll();
    }

    @GetMapping("/basic")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ListsCountriesResponse> listAllBasicsCountries() {
        return super.listBasics();
    }

    @GetMapping("/{countryId}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.LOCATIONS)
    public ResponseEntity<CountryResponse> findCountryById(
            @PathVariable UUID countryId
    ) {
        return super.findById(countryId);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.CREATE_COUNTRY)
    public ResponseEntity<MessageResponse<CountryResponse>> createCountry(
            @RequestBody @Valid CountryRequest countryRequest
    ) {
        return super.create(countryRequest);
    }

    @PutMapping("/update/{countryId}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_COUNTRY)
    public ResponseEntity<MessageResponse<CountryResponse>> updateCountry(
            @PathVariable UUID countryId,
            @RequestBody @Valid CountryRequest countryRequest
    ) {
        return super.update(countryId, countryRequest);
    }

    @Override
    public ListsCountriesResponse createListsResponse(List<BasicCountryResponse> enables, List<BasicCountryResponse> disables) {
        return ListsCountriesResponse
                .builder()
                .enables(enables)
                .disables(disables)
                .build();
    }

    @Override
    public WebSocketMessageValue getWebSocketMessageValue() {
        return WebSocketMessageValue.LIST_COUNTRIES;
    }

    @Override
    public ModuleOption getModuleOption() {
        return ModuleOption.LOCATIONS;
    }

}
