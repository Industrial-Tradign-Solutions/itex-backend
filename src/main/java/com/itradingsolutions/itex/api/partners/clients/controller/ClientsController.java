package com.itradingsolutions.itex.api.partners.clients.controller;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleOption;
import com.itradingsolutions.itex.api.common.controller.CommonController;
import com.itradingsolutions.itex.api.common.models.enums.OpenAndLockType;
import com.itradingsolutions.itex.api.common.util.models.enums.HistoryActions;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import com.itradingsolutions.itex.api.masters.industry.models.responses.BasicIndustryResponse;
import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientDTO;
import com.itradingsolutions.itex.api.partners.clients.models.enums.ClientStatus;
import com.itradingsolutions.itex.api.partners.clients.models.filter.FilterListClients;
import com.itradingsolutions.itex.api.partners.clients.models.mappers.ClientMapper;
import com.itradingsolutions.itex.api.partners.clients.models.requests.ClientRequest;
import com.itradingsolutions.itex.api.partners.clients.models.responses.BasicClientResponse;
import com.itradingsolutions.itex.api.partners.clients.models.responses.ClientDashboardResponse;
import com.itradingsolutions.itex.api.partners.clients.models.responses.ClientResponse;
import com.itradingsolutions.itex.api.partners.clients.models.responses.ListClientResponse;
import com.itradingsolutions.itex.api.partners.clients.services.IClientService;
import com.itradingsolutions.itex.config.security.auth.AccessToAction;
import com.itradingsolutions.itex.config.security.auth.AccessToModule;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/partners/clients")
@Validated
@RequiredArgsConstructor
public class ClientsController extends CommonController {

        private final IClientService clientService;
        private final ClientMapper clientMapper;

        @GetMapping("/dashboard")
        @ResponseStatus(HttpStatus.OK)
        @Deprecated
        public ResponseEntity<ClientDashboardResponse> getDashboardClients() {
                return ResponseEntity.ok(clientService.dashboardClients());
        }


        @GetMapping("/load-open-clients")
        @ResponseStatus(HttpStatus.OK)
        @AccessToModule(option = ModuleOption.CLIENTS)
        public ResponseEntity<List<ListClientResponse>> loadOpenClients() {
                var list = clientService.listAllOpenClients(getUserAuthenticated());
                return ResponseEntity.ok(
                        list.stream().map(client ->
                                new ListClientResponse(
                                        client.getId(),
                                        null,
                                        client.getName(),
                                        null,
                                        null,
                                        null,
                                        null,
                                        null
                                )
                        ).toList()
                );
        }

        @PutMapping("/close-list-clients")
        @ResponseStatus(HttpStatus.OK)
        @AccessToModule(option = ModuleOption.CLIENTS)
        public ResponseEntity<MessageResponse<List<UUID>>> closeListClients(
                @RequestBody List<UUID> listClientsIds
        ) {
                listClientsIds.forEach(clientId -> {
                        clientService.unlockClient(clientId);
                        historyService.saveHistory(HistoryActions.UNLOCK, ModuleOption.CLIENTS, clientId, null, null);
                });
                return ResponseEntity
                        .ok(
                                new MessageResponse<>(
                                        SUCCESS_TITLE,
                                        simpleMessage("client.all-closed"),
                                        listClientsIds
                                )
                        );
        }

        @PutMapping("/close-client/{clientId}")
        @ResponseStatus(HttpStatus.OK)
        @AccessToModule(option = ModuleOption.CLIENTS)
        public ResponseEntity<MessageResponse<UUID>> closeClient(
                @PathVariable UUID clientId) {
                clientService.unlockClient(clientId);
                historyService.saveHistory(HistoryActions.UNLOCK, ModuleOption.CLIENTS, clientId, null, null);
                return ResponseEntity
                        .ok(
                                new MessageResponse<>(
                                        SUCCESS_TITLE,
                                        simpleMessage("client.closed"),
                                        clientId
                                )
                        );
        }

        @GetMapping("/open-and-lock/{clientId}")
        @ResponseStatus(HttpStatus.OK)
        @AccessToModule(option = ModuleOption.CLIENTS)
        public ResponseEntity<Map<String, Object>> openAndLockClients(@PathVariable UUID clientId, @RequestParam OpenAndLockType type) {
                Map<String, Object> resp = new HashMap<>();
                var client = clientService.openAndLockClient(clientId, type);

                resp.put("data", clientMapper.dtoToResponse(client));
                var isValidOpen = isOpenByUsername(client.getOpenBy(), type);
                resp.put("isValidOpen", isValidOpen);
                if (isValidOpen)
                        historyService.saveHistory(HistoryActions.LOCK, ModuleOption.CLIENTS, clientId, null, null);
                return ResponseEntity.ok(resp);
        }

        @PostMapping("/create")
        @ResponseStatus(HttpStatus.CREATED)
        @AccessToAction(action = ModuleAction.CREATE_CLIENT)
        public ResponseEntity<MessageResponse<ClientResponse>> create(
                @RequestBody @Valid ClientRequest clientRequest
        ) {
                var resp = clientService.createClient(clientRequest);
                historyService.saveHistory(HistoryActions.CREATE, ModuleOption.CLIENTS, resp.getId(), null, resp);
                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(
                                new MessageResponse<>(
                                        SUCCESS_TITLE,
                                        simpleMessage("client.created"),
                                        clientMapper.dtoToResponse(resp)
                                )
                        );
        }

        @PutMapping("/update/{clientId}")
        @ResponseStatus(HttpStatus.OK)
        @AccessToAction(action = ModuleAction.UPDATE_CLIENT)
        public ResponseEntity<MessageResponse<ClientResponse>> update(
                @PathVariable UUID clientId,
                @RequestBody @Valid ClientRequest clientRequest
        ) {
                var oldClient = clientMapper.entityToDto(clientService.findClientById(clientId, false));
                oldClient.setInfoByDepartment(oldClient.getInfoByDepartment().stream().filter(info -> info.getDepartment().isClientInfo()).toList());
                var resp = clientService.updateClient(clientRequest, clientId);
                resp.setInfoByDepartment(resp.getInfoByDepartment().stream().filter(info -> info.getDepartment().isClientInfo()).toList());
                historyService.saveHistory(HistoryActions.UPDATE, ModuleOption.CLIENTS, resp.getId(), oldClient, resp);
                saveHistoryStatus(resp, oldClient);
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(
                                new MessageResponse<>(
                                        SUCCESS_TITLE,
                                        simpleMessage("client.updated"),
                                        clientMapper.dtoToResponse(resp)
                                )
                        );
        }

        private void saveHistoryStatus(ClientDTO newClient, ClientDTO oldClient) {
                if (!oldClient.getStatus().equals(newClient.getStatus())) {
                        var action = HistoryActions.CHANGE_STATUS;
                        if (newClient.getStatus().equals(ClientStatus.ACTIVE)) {
                                if (oldClient.getStatus().equals(ClientStatus.INACTIVE)) {
                                        action = HistoryActions.ENABLE;
                                }
                        } else if (newClient.getStatus().equals(ClientStatus.INACTIVE)) {
                                action = HistoryActions.DISABLE;
                        }
                        historyService.saveHistory(action, ModuleOption.CLIENTS, newClient.getId(), null, null);
                }
        }

    @GetMapping("/list-active")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<BasicClientResponse>> listAllActive() {
        var resp = clientService.listAllActive();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(resp.stream().map(clientMapper::dtoToBasicResponse).toList());
    }

        @GetMapping
        @ResponseStatus(HttpStatus.OK)
        @AccessToModule(option = ModuleOption.CLIENTS)
        public ResponseEntity<Page<ListClientResponse>> listAll(
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size,
                @ModelAttribute FilterListClients filters
        ) {
                var resp = clientService.listAllClients(filters.getPageRequest(page, size), filters);

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(
                                new PageImpl<>(resp.getContent().stream().map(client ->
                                        new ListClientResponse(
                                                client.getId(),
                                                client.getCode(),
                                                client.getName(),
                                                client.getTaxId(),
                                                client.getCity() != null ? client.getCity().getFullName() : null,
                                                client.getAddress(),
                                                client.getIndustry() != null ?
                                                BasicIndustryResponse.builder()
                                                        .id(client.getIndustry().getId())
                                                        .name(client.getIndustry().getName())
                                                        .active(client.getIndustry().isActive())
                                                        .build()
                                                : null,
                                                client.getStatus()
                                        )
                                ).toList(),resp.getPageable(),resp.getTotalElements())
                        );
        }
}
