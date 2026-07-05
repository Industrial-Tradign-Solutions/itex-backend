package com.itradingsolutions.itex.api.ip.qr.controllers;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleOption;
import com.itradingsolutions.itex.api.common.controller.CommonController;
import com.itradingsolutions.itex.api.common.models.enums.OpenAndLockType;
import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import com.itradingsolutions.itex.api.ip.qr.models.enums.IpQuoteRequestHistoryAction;
import com.itradingsolutions.itex.api.ip.qr.models.enums.IpQuoteRequestStatus;
import com.itradingsolutions.itex.api.ip.qr.models.filters.FilterListIpQuoteRequest;
import com.itradingsolutions.itex.api.ip.qr.models.mappers.IpQuoteRequestHistoryMapper;
import com.itradingsolutions.itex.api.ip.qr.models.mappers.IpQuoteRequestMapper;
import com.itradingsolutions.itex.api.ip.qr.models.requests.IpQuoteRequestRequest;
import com.itradingsolutions.itex.api.ip.qr.models.responses.IpQuoteRequestHistoryResponse;
import com.itradingsolutions.itex.api.ip.qr.models.responses.IpQuoteRequestResponse;
import com.itradingsolutions.itex.api.ip.qr.models.responses.ListIpQuoteRequestResponse;
import com.itradingsolutions.itex.api.ip.qr.models.responses.OpenLockIpQuoteRequestResponse;
import com.itradingsolutions.itex.api.ip.qr.service.IIpQuoteRequestHistoryService;
import com.itradingsolutions.itex.api.ip.qr.service.IIpQuoteRequestService;
import com.itradingsolutions.itex.config.security.auth.AccessToAction;
import com.itradingsolutions.itex.config.security.auth.AccessToModule;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RequestMapping("/ip/qr")
@Validated
@AllArgsConstructor
public class IpQuoteRequestController extends CommonController {

    private final IIpQuoteRequestHistoryService qrHistoryService;
    private final IpQuoteRequestHistoryMapper qrHistoryMapper;
    private final IpQuoteRequestMapper qrMapper;
    private final IIpQuoteRequestService qrService;

    @GetMapping("/history/{qr_id}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.VIEW_HISTORY_IP_QUOTE_REQUESTS)
    public ResponseEntity<List<IpQuoteRequestHistoryResponse>> getHistory(@PathVariable("qr_id") UUID idQR)  {
        var resp = qrHistoryService.getHistoryById(idQR);
        return ResponseEntity.ok(
                resp.stream().map(qrHistoryMapper::dtoToResponse).toList()
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.CREATE_IP_QUOTE_REQUESTS)
    public ResponseEntity<MessageResponse<IpQuoteRequestResponse>> createQuoteRequest(
            @RequestBody @Valid IpQuoteRequestRequest request
    ) {
        var resp = qrService.createIpQuoteRequest(qrMapper.requestToDTO(request));
        qrHistoryService.addHistory(IpQuoteRequestHistoryAction.CREATE, null, resp);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new MessageResponse<>(
                                SUCCESS_TITLE,
                                simpleMessage("ip.qr.created"),
                                qrMapper.dtoToResponse(resp)
                        )
                );
    }

    @PatchMapping("/clone/{qr_id}")
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.CLONE_IP_QUOTE_REQUESTS)
    public ResponseEntity<MessageResponse<ListIpQuoteRequestResponse>> cloneQuoteRequest(@PathVariable(name = "qr_id") UUID idQr) {
        var original = qrService.findIpQuoteRequestById(idQr);
        var resp = qrService.cloneIpQuoteRequest(idQr);
        qrHistoryService.addHistory(IpQuoteRequestHistoryAction.CLONE, original, resp);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new MessageResponse<>(
                                SUCCESS_TITLE,
                                simpleMessage("ip.qr.cloned"),
                                qrMapper.dtoToListResponse(resp)
                        )
                );
    }

    @PutMapping("/{qr_id}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_IP_QUOTE_REQUESTS)
    public ResponseEntity<MessageResponse<IpQuoteRequestResponse>> updateQuoteRequest(
            @PathVariable("qr_id") UUID idQuoteRequest,
            @RequestBody @Valid IpQuoteRequestRequest request
    ) {
        var oldQr = qrService.findIpQuoteRequestById(idQuoteRequest);
        var resp = qrService.updateIpQuoteRequestById(idQuoteRequest, qrMapper.requestToDTO(request));
        qrHistoryService.addHistory(IpQuoteRequestHistoryAction.UPDATE, oldQr, resp);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponse<>(
                                SUCCESS_TITLE,
                                simpleMessage("ip.qr.updated"),
                                qrMapper.dtoToResponse(resp)
                        )
                );
    }

    @PatchMapping("/open-lock/{id_quote_request}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.IP_QUOTE_REQUESTS)
    public ResponseEntity<OpenLockIpQuoteRequestResponse> openLockQuoteRequest(
            @PathVariable(name = "id_quote_request") UUID idQuoteRequest,
            @RequestParam OpenAndLockType type
    ) {
        var quoteRequest = qrService.openAndLockIpQuoteRequest(idQuoteRequest, type );
        return ResponseEntity.status(HttpStatus.OK).body(
                new OpenLockIpQuoteRequestResponse(
                        qrMapper.dtoToResponse(quoteRequest),
                        isOpenByUsername(quoteRequest.getOpenBy(), type)
                )
        );
    }

    @PatchMapping("/close/{id_quote_request}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.IP_QUOTE_REQUESTS)
    public ResponseEntity<MessageResponse<UUID>> closeQuoteRequest(
            @PathVariable(name = "id_quote_request") UUID idQuoteRequest
    ) {
        qrService.unlockIpQuoteRequest(idQuoteRequest);
        return ResponseEntity.status(HttpStatus.OK).body(
                new MessageResponse<>(
                        SUCCESS_TITLE,
                        simpleMessage("ip.qr.closed"),
                        idQuoteRequest
                ));
    }

    @GetMapping("/load-open")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.IP_QUOTE_REQUESTS)
    public ResponseEntity<List<ListIpQuoteRequestResponse>> loadOpenQuoteRequests() {
        var list = qrService.listAllOpenIpQuoteRequest(getUserAuthenticated());
        return ResponseEntity.ok(
                list.stream().map(qrMapper::dtoToListResponse).toList()
        );
    }

    @PatchMapping("/close-list")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.IP_QUOTE_REQUESTS)
    public ResponseEntity<MessageResponse<List<UUID>>> closeListIpQuoteRequests() {
        var list = qrService.listAllOpenIpQuoteRequest(getUserAuthenticated());
        list.forEach(item -> {
            if (item != null)
                qrService.unlockIpQuoteRequest(item.getId());
        });
        return ResponseEntity
            .ok(
                    new MessageResponse<>(
                            SUCCESS_TITLE,
                            simpleMessage("ip.qr.all-closed"),
                            list.stream().map(BaseDTO::getId).toList()
                    )
            );
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.IP_QUOTE_REQUESTS)
    public ResponseEntity<Page<ListIpQuoteRequestResponse>> listAllQuoteRequest(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @ModelAttribute FilterListIpQuoteRequest filters
    ) {
        var resp = qrService.listAllQuoteRequests(filters.getPageRequest(page, size), filters);
        var list = resp.getContent().stream()
                .map(qrMapper::dtoToListResponse).toList();
        return ResponseEntity.ok(new PageImpl<>(list, resp.getPageable(),resp.getTotalElements()));
    }

    @GetMapping("/print/{id_quote_request}")
    @AccessToModule(option = ModuleOption.IP_QUOTE_REQUESTS)
    public ResponseEntity<byte[]> printQuoteRequest(
            @PathVariable("id_quote_request") UUID idQuoteRequest
    ) {
        byte[] pdfBytes = qrService.printQuoteRequest(idQuoteRequest);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "quote_request_" + idQuoteRequest + ".pdf");
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @PatchMapping("/{id_quote_request}/change-status")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.IP_QUOTE_REQUESTS)
    public ResponseEntity<MessageResponse<ListIpQuoteRequestResponse>> changeStatusQuoteRequest(
            @PathVariable("id_quote_request") UUID idQuoteRequest,
            @RequestParam(name = "status") IpQuoteRequestStatus status
    ) {
        if (status.equals(IpQuoteRequestStatus.REJECTED))
            throw new IllegalArgumentException("Cannot change status to REJECTED");
        var oldQr = qrService.findIpQuoteRequestById(idQuoteRequest);
        var resp =qrService.changeStatusQuoteRequest(idQuoteRequest, status);
        qrHistoryService.addHistory(IpQuoteRequestHistoryAction.UPDATE, oldQr, resp);
        return ResponseEntity
                .ok(
                        new MessageResponse<>(
                                SUCCESS_TITLE,
                                simpleMessage("ip.qr.change-status"),
                                qrMapper.dtoToListResponse(resp)
                        )
                );
    }

    @DeleteMapping("/{id_quote_request}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.REJECT_IP_QUOTE_REQUESTS)
    public ResponseEntity<MessageResponse<ListIpQuoteRequestResponse>> rejectQuoteRequest(
            @PathVariable("id_quote_request") UUID idQuoteRequest
    ) {
        var oldQr = qrService.findIpQuoteRequestById(idQuoteRequest);
        var resp = qrService.rejectQuoteRequest(idQuoteRequest);
        qrHistoryService.addHistory(IpQuoteRequestHistoryAction.REJECTED, oldQr, resp);
        return ResponseEntity
                .ok(
                        new MessageResponse<>(
                                SUCCESS_TITLE,
                                simpleMessage("ip.qr.rejected"),
                                qrMapper.dtoToListResponse(resp)
                        )
                );
    }

    @GetMapping("/available-for-quotation/{id_client}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.CREATE_IP_QUOTATIONS)
    public ResponseEntity<List<ListIpQuoteRequestResponse>> getAvailableForQuotation(
            @PathVariable(name = "id_client") UUID idClient,
            @RequestParam(name = "view-completed-qr", defaultValue = "false") boolean viewCompletedQR,
            @RequestParam(name = "currency", defaultValue = "USD") Currency currency
    ) {
        var listQR = qrService.getListQuoteRequestByClientAvailableToQuotation(idClient, viewCompletedQR, currency);
        return ResponseEntity.status(HttpStatus.OK).body(
                listQR.stream().map(qrMapper::dtoToListResponse).toList()
        );
    }
}
