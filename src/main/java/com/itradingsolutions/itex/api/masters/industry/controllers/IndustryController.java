package com.itradingsolutions.itex.api.masters.industry.controllers;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleOption;
import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.masters.common.controller.CommonMasterController;
import com.itradingsolutions.itex.api.masters.industry.models.dto.IndustryDTO;
import com.itradingsolutions.itex.api.masters.industry.models.mappers.IndustryMapper;
import com.itradingsolutions.itex.api.masters.industry.models.requests.IndustryRequest;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import com.itradingsolutions.itex.api.masters.industry.models.responses.BasicIndustryResponse;
import com.itradingsolutions.itex.api.masters.industry.models.responses.IndustryResponse;
import com.itradingsolutions.itex.api.masters.industry.models.responses.ListsIndustryResponse;
import com.itradingsolutions.itex.api.masters.industry.services.IIndustryService;
import com.itradingsolutions.itex.config.security.auth.AccessToAction;
import com.itradingsolutions.itex.config.security.auth.AccessToModule;
import com.itradingsolutions.itex.config.websocket.WebSocketMessageValue;
import jakarta.validation.Valid;
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
@RequestMapping("/master/industries")
@Validated
public class IndustryController extends CommonMasterController<
        IIndustryService,
        IndustryDTO,
        IndustryMapper,
        BasicIndustryResponse,
        ListsIndustryResponse,
        IndustryResponse,
        IndustryRequest> {


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.INDUSTRIES)
    public ResponseEntity<List<IndustryResponse>> listAllIndustries() {
        return super.listAll();
    }


    @GetMapping("/basic")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ListsIndustryResponse> listAllBasicIndustries() {
        return super.listBasics();
    }

    @GetMapping("/{industryId}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.INDUSTRIES)
    public ResponseEntity<IndustryResponse> findIndustryById(
            @PathVariable UUID industryId
    ) {
        return super.findById(industryId);
    }


    @DeleteMapping("/disable/{industryId}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.DISABLE_INDUSTRY)
    public ResponseEntity<MessageResponse<UUID>> disableIndustry(
            @PathVariable UUID industryId
    ) {
        return super.disable(industryId);
    }


    @PatchMapping("/enable/{industryId}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.ENABLE_INDUSTRY)
    public ResponseEntity<MessageResponse<UUID>> enableIndustry(
            @PathVariable UUID industryId
    ) {
        return super.enable(industryId);
    }


    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.CREATE_INDUSTRY)
    public ResponseEntity<MessageResponse<IndustryResponse>> createIndustry(
            @RequestBody @Valid IndustryRequest industryRequest
    ) {
        return super.create(industryRequest);
    }


    @PutMapping("/update/{industryId}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_INDUSTRY)
    public ResponseEntity<MessageResponse<IndustryResponse>> updateIndustry(
            @PathVariable UUID industryId,
            @RequestBody @Valid IndustryRequest industryRequest
    ) {
        return super.update(industryId, industryRequest);
    }

    @Override
    public ListsIndustryResponse createListsResponse(
            List<BasicIndustryResponse> enables,
            List<BasicIndustryResponse> disables) {
        return ListsIndustryResponse
                .builder()
                .enables(enables)
                .disables(disables)
                .build();
    }

    @Override
    public WebSocketMessageValue getWebSocketMessageValue() {
        return WebSocketMessageValue.LIST_INDUSTRIES;
    }

    @Override
    public ModuleOption getModuleOption() {
        return ModuleOption.INDUSTRIES;
    }

}
