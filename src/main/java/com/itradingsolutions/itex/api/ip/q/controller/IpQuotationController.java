package com.itradingsolutions.itex.api.ip.q.controller;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleOption;
import com.itradingsolutions.itex.api.common.controller.CommonController;
import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;
import com.itradingsolutions.itex.api.common.models.enums.OpenAndLockType;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import com.itradingsolutions.itex.api.ip.q.models.filters.FilterListIpQuotation;
import com.itradingsolutions.itex.api.ip.q.models.mapper.IpQuotationHistoryMapper;
import com.itradingsolutions.itex.api.ip.q.models.mapper.IpQuotationMapper;
import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationHistoryAction;
import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationStatus;
import com.itradingsolutions.itex.api.ip.q.models.requests.AddQuoteRequestsToQuotationRequest;
import com.itradingsolutions.itex.api.ip.q.models.requests.CreateIpQuotationRequest;
import com.itradingsolutions.itex.api.ip.q.models.requests.OpenLockIpQuotationResponse;
import com.itradingsolutions.itex.api.ip.q.models.requests.UpdateIpQuotationRequest;
import com.itradingsolutions.itex.api.ip.q.models.response.CreateQuotationResponse;
import com.itradingsolutions.itex.api.ip.q.models.response.IpQuotationHistoryResponse;
import com.itradingsolutions.itex.api.ip.q.models.response.IpQuotationResponse;
import com.itradingsolutions.itex.api.ip.q.models.response.ListIpQuotationResponse;
import com.itradingsolutions.itex.api.ip.q.service.IIpQuotationHistoryService;
import com.itradingsolutions.itex.api.ip.q.service.IpQuotationService;
import com.itradingsolutions.itex.config.security.auth.AccessToAction;
import com.itradingsolutions.itex.config.security.auth.AccessToModule;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
@RequestMapping("/ip/q")
@Validated
@AllArgsConstructor
public class IpQuotationController extends CommonController {

    private final IpQuotationMapper quotationMapper;
    private final IpQuotationService quotationService;
    private final IIpQuotationHistoryService historyService;
    private final IpQuotationHistoryMapper historyMapper;


    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.CREATE_IP_QUOTATIONS)
    public ResponseEntity<MessageResponse<CreateQuotationResponse>> createQuotation(
            @RequestBody CreateIpQuotationRequest request
    ) {
        var resp = quotationService.createQuotation(request);
        return ResponseEntity.status(HttpStatus.OK).body(
            new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("ip.q.created"),
                new CreateQuotationResponse(
                    quotationMapper.dtoToListResponse(resp),
                    OpenAndLockType.EDIT,
                    true
                )
            )
        );
    }

    @PatchMapping("/close/{id_quotation}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.IP_QUOTATIONS)
    public ResponseEntity<MessageResponse<UUID>> closeQuotation(
            @PathVariable(name = "id_quotation") UUID idQuotation
    ) {
        quotationService.unlockIpQuotation(idQuotation);
        return ResponseEntity.status(HttpStatus.OK).body(
                new MessageResponse<>(
                        SUCCESS_TITLE,
                        simpleMessage("ip.q.closed"),
                        idQuotation
                ));
    }

    @GetMapping("/load-open")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.IP_QUOTATIONS)
    public ResponseEntity<List<ListIpQuotationResponse>> loadOpenQuotations() {
        var list = quotationService.listAllOpenIpQuotation(getUserAuthenticated());
        return ResponseEntity.ok(
                list.stream().map(quotationMapper::dtoToListResponse).toList()
        );
    }

    @PatchMapping("/close-list")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.IP_QUOTATIONS)
    public ResponseEntity<MessageResponse<List<UUID>>> closeListIpQuotations() {
        var list = quotationService.listAllOpenIpQuotation(getUserAuthenticated());
        list.forEach(item -> {
            if (item != null)
                quotationService.unlockIpQuotation(item.getId());
        });
        return ResponseEntity
                .ok(
                        new MessageResponse<>(
                                SUCCESS_TITLE,
                                simpleMessage("ip.q.all-closed"),
                                list.stream().map(BaseDTO::getId).toList()
                        )
                );
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.IP_QUOTATIONS)
    public ResponseEntity<Page<ListIpQuotationResponse>> listAllQuotations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @ModelAttribute FilterListIpQuotation filters
    ) {
        var resp = quotationService.listAllQuotations(filters.getPageRequest(page, size), filters);
        var list = resp.getContent().stream()
                .map(quotationMapper::dtoToListResponse).toList();
        return ResponseEntity.ok(new PageImpl<>(list, resp.getPageable(),resp.getTotalElements()));
    }

    @PatchMapping("/open-lock/{id_quotation}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.IP_QUOTATIONS)
    public ResponseEntity<OpenLockIpQuotationResponse> openLockQuotation(
            @PathVariable(name = "id_quotation") UUID idQuotation,
            @RequestParam OpenAndLockType type
    ) {
        var quotation = quotationService.openAndLockIpQuotation(idQuotation, type );
        return ResponseEntity.status(HttpStatus.OK).body(
            new OpenLockIpQuotationResponse(
                quotationMapper.dtoToResponse(quotation),
                isOpenByUsername(quotation.getOpenBy(), type)
            )
        );
    }

    @PutMapping("/{id_quotation}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_IP_QUOTATIONS)
    public ResponseEntity<MessageResponse<IpQuotationResponse>> updateQuotation(
            @PathVariable(name = "id_quotation") UUID idQuotation,
            @RequestBody @Valid UpdateIpQuotationRequest request
    ) {
        var resp = quotationService.updateQuotation(idQuotation, request);
        return ResponseEntity.ok(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("ip.q.updated"),
                quotationMapper.dtoToResponse(resp)
        ));
    }

    @PatchMapping("/change-status/{id_quotation}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.IP_QUOTATIONS)
    public ResponseEntity<MessageResponse<IpQuotationResponse>> changeStatusQuotation(
            @PathVariable(name = "id_quotation") UUID idQuotation,
            @RequestParam IpQuotationStatus status
    ) {
        var resp = quotationService.changeStatusQuotation(idQuotation, status);
        return ResponseEntity.ok(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("ip.q.change-status"),
                quotationMapper.dtoToResponse(resp)
        ));
    }

    @PatchMapping("/reject/{id_quotation}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.REJECT_IP_QUOTATIONS)
    public ResponseEntity<MessageResponse<IpQuotationResponse>> rejectQuotation(
            @PathVariable(name = "id_quotation") UUID idQuotation
    ) {
        var resp = quotationService.rejectQuotation(idQuotation);
        return ResponseEntity.ok(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("ip.q.rejected"),
                quotationMapper.dtoToResponse(resp)
        ));
    }

    @DeleteMapping("/{id_quotation}/quote-request/{id_qqr}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_IP_QUOTATIONS)
    public ResponseEntity<MessageResponse<UUID>> removeQuoteRequestFromQuotation(
            @PathVariable(name = "id_quotation") UUID idQuotation,
            @PathVariable(name = "id_qqr") UUID idQqr
    ) {
        var oldDto = quotationService.getQuotationForHistory(idQuotation);
        quotationService.removeQuoteRequestFromQuotation(idQuotation, idQqr);
        historyService.addHistory(IpQuotationHistoryAction.REMOVE_QR, null, oldDto);
        return ResponseEntity.ok(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("ip.q.qr.removed"),
                idQqr
        ));
    }

    @PostMapping("/{id_quotation}/quote-request")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_IP_QUOTATIONS)
    public ResponseEntity<MessageResponse<IpQuotationResponse>> addQuoteRequestsToQuotation(
            @PathVariable(name = "id_quotation") UUID idQuotation,
            @RequestBody @Valid AddQuoteRequestsToQuotationRequest request
    ) {
        var resp = quotationService.addQuoteRequestsToQuotation(idQuotation, request.quoteRequestIds());
        historyService.addHistory(IpQuotationHistoryAction.ADD_QR, null, resp);
        return ResponseEntity.ok(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("ip.q.qr.added"),
                quotationMapper.dtoToResponse(resp)
        ));
    }

    @GetMapping("/history/{id_quotation}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.VIEW_HISTORY_IP_QUOTATIONS)
    public ResponseEntity<List<IpQuotationHistoryResponse>> getHistory(
            @PathVariable(name = "id_quotation") UUID idQuotation
    ) {
        var history = historyService.getHistoryById(idQuotation);
        return ResponseEntity.ok(
                history.stream()
                        .map(historyMapper::dtoToResponse)
                        .toList()
        );
    }

    @PatchMapping("/clone/{id_quotation}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.CLONE_IP_QUOTATIONS)
    public ResponseEntity<MessageResponse<IpQuotationResponse>> cloneQuotation(
            @PathVariable(name = "id_quotation") UUID idQuotation
    ) {
        var cloned = quotationService.cloneQuotation(idQuotation);
        return ResponseEntity.ok(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("ip.q.cloned"),
                quotationMapper.dtoToResponse(cloned)
        ));
    }
}
