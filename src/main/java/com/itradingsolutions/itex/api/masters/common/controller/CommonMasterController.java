package com.itradingsolutions.itex.api.masters.common.controller;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleOption;
import com.itradingsolutions.itex.api.common.controller.CommonController;
import com.itradingsolutions.itex.api.common.models.responses.ListsResponses;
import com.itradingsolutions.itex.api.common.service.CommonService;
import com.itradingsolutions.itex.api.common.util.models.enums.HistoryActions;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import com.itradingsolutions.itex.api.masters.common.models.dto.BaseMasterDTO;
import com.itradingsolutions.itex.api.masters.common.models.mappers.CommonMasterMapper;
import com.itradingsolutions.itex.api.masters.common.models.requests.BaseMasterRequest;
import com.itradingsolutions.itex.api.masters.common.models.responses.BaseBasicMasterResponse;
import com.itradingsolutions.itex.config.websocket.WebSocketMessage;
import com.itradingsolutions.itex.config.websocket.WebSocketMessageValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public abstract class CommonMasterController<
        S extends CommonService<D>,
        D extends BaseMasterDTO,
        M extends CommonMasterMapper<D, B, R, Q>,
        B extends BaseBasicMasterResponse,
        L extends ListsResponses<B>,
        R extends BaseBasicMasterResponse,
        Q extends BaseMasterRequest
        >
extends CommonController {

    @Autowired
    protected S service;

    @Autowired
    protected M mapper;

    protected ResponseEntity<MessageResponse<UUID>> disable(UUID id) {
        service.disable(id);
        sendMessageSocket();
        historyService.saveHistory(HistoryActions.DISABLE, getModuleOption(), id, null, null);
        return ResponseEntity
                .status(HttpStatus.OK).body(
                        new MessageResponse<>(
                                SUCCESS_TITLE,
                                compositeMessage("common.master.disable", new String[]{getModuleOption().getOptionName().toLowerCase()}),
                                id
                        )
                );
    }

    protected ResponseEntity<MessageResponse<UUID>> enable(UUID id) {
        service.enable(id);
        sendMessageSocket();
        historyService.saveHistory(HistoryActions.ENABLE, getModuleOption(), id, null, null);
        return ResponseEntity
                .status(HttpStatus.OK).body(
                        new MessageResponse<>(
                                SUCCESS_TITLE,
                                compositeMessage("common.master.enable", new String[]{getModuleOption().getOptionName().toLowerCase()}),
                                id
                        )
                );
    }

    protected ResponseEntity<L> listBasics() {
        return ResponseEntity.ok(getBasic());
    }

    protected ResponseEntity<List<R>> listAll() {
        var list = service.listAll();
        var resp = list.stream()
                .map(mapper::dtoToResponse)
                .toList();
        return ResponseEntity.ok(resp);
    }

    protected ResponseEntity<R> findById(UUID id) {
        var department = service.findById(id, true);
        return ResponseEntity.ok(mapper.dtoToResponse(department));
    }

    protected ResponseEntity<MessageResponse<R>> create(Q request) {
        var dto = mapper.requestToDTO(request);
        var resp = service.create(dto);
        sendMessageSocket();
        historyService.saveHistory(HistoryActions.CREATE, getModuleOption(), resp.getId(), null, resp);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new MessageResponse<>(
                                SUCCESS_TITLE,
                                compositeMessage("common.master.create", new String[]{getModuleOption().getOptionName().toLowerCase()}),
                                mapper.dtoToResponse(resp)
                        )
                );
    }

    protected ResponseEntity<MessageResponse<R>> update(UUID id, Q request) {
        var old = service.findById(id, true);
        var dto = mapper.requestToDTO(request);
        var resp = service.update(dto, id);
        sendMessageSocket();
        historyService.saveHistory(HistoryActions.UPDATE, getModuleOption(), id, old, resp);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new MessageResponse<>(
                                SUCCESS_TITLE,
                                compositeMessage("common.master.update", new String[]{getModuleOption().getOptionName().toLowerCase()}),
                                mapper.dtoToResponse(resp)
                        )
                );
    }

    private L getBasic() {
        var list = service.listAll();

        var enables = list.stream().filter(BaseMasterDTO::isActive).map(mapper::dtoToResponseBasic).toList();
        var disables = list.stream().filter(item -> !item.isActive()).map(item -> {
            item.setName(item.getName() + " (DISABLED)");
            return mapper.dtoToResponseBasic(item);
        }).toList();

        return createListsResponse(enables, disables);
    }

    private void sendMessageSocket() {
        new Thread(() -> socketHandler.sendMessage(new WebSocketMessage<>(getBasic(), getWebSocketMessageValue()))).start();
    }

    public abstract L createListsResponse(List<B> enables, List<B> disables);
    public abstract WebSocketMessageValue getWebSocketMessageValue();
    public abstract ModuleOption getModuleOption();
}
