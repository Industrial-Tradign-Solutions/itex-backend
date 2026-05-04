package com.itradingsolutions.itex.api.ip.q.controller;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.common.controller.CommonController;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationHistoryAction;
import com.itradingsolutions.itex.api.ip.q.models.mapper.IpQuotationMapper;
import com.itradingsolutions.itex.api.ip.q.models.requests.AddQuoteRequestsToQuotationRequest;
import com.itradingsolutions.itex.api.ip.q.models.response.IpQuotationResponse;
import com.itradingsolutions.itex.api.ip.q.service.IIpQuotationHistoryService;
import com.itradingsolutions.itex.api.ip.q.service.IpQuotationService;
import com.itradingsolutions.itex.api.ip.qr.models.mappers.IpQuoteRequestMapper;
import com.itradingsolutions.itex.api.ip.qr.models.responses.ListIpQuoteRequestResponse;
import com.itradingsolutions.itex.api.ip.qr.service.IIpQuoteRequestService;
import com.itradingsolutions.itex.config.security.auth.AccessToAction;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ip/q/{id_quotation}/quote-request")
@Validated
@AllArgsConstructor
public class IpQuotationQuteRequestController extends CommonController {

    private final IIpQuoteRequestService quoteRequestService;
    private final IpQuoteRequestMapper quoteRequestMapper;
    private final IpQuotationService quotationService;
    private final IpQuotationMapper quotationMapper;
    private final IIpQuotationHistoryService qHistoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_IP_QUOTATIONS)
    public ResponseEntity<MessageResponse<IpQuotationResponse>> addQuoteRequestsToQuotation(
            @PathVariable(name = "id_quotation") UUID idQuotation,
            @RequestBody @Valid AddQuoteRequestsToQuotationRequest request
    ) {
        var resp = quotationService.addQuoteRequestsToQuotation(idQuotation, request.quoteRequestIds());
        qHistoryService.addHistory(IpQuotationHistoryAction.ADD_QR, null, resp);
        return ResponseEntity.ok(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("ip.q.qr.added"),
                quotationMapper.dtoToResponse(resp)
        ));
    }

    @DeleteMapping("/{id_qqr}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_IP_QUOTATIONS)
    public ResponseEntity<MessageResponse<UUID>> removeQuoteRequestFromQuotation(
            @PathVariable(name = "id_quotation") UUID idQuotation,
            @PathVariable(name = "id_qqr") UUID idQqr
    ) {
        var oldDto = quotationService.getQuotationForHistory(idQuotation);
        quotationService.removeQuoteRequestFromQuotation(idQuotation, idQqr);
        qHistoryService.addHistory(IpQuotationHistoryAction.REMOVE_QR, null, oldDto);
        return ResponseEntity.ok(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("ip.q.qr.removed"),
                idQqr
        ));
    }

    @GetMapping("/available/{id_client}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.CREATE_IP_QUOTATIONS)
    public ResponseEntity<List<ListIpQuoteRequestResponse>> getListQuoteRequestByClientAvailableToQuotation(
            @PathVariable(name = "id_client") UUID idClient,
            @RequestParam(name = "view-completed-qr", defaultValue = "false") boolean viewCompletedQR,
            @RequestParam(name = "currency", defaultValue = "USD") Currency currency
    ) {
        var listQR = quoteRequestService.getListQuoteRequestByClientAvailableToQuotation(idClient, viewCompletedQR, currency);
        return ResponseEntity.status(HttpStatus.OK).body(
            listQR.stream().map(quoteRequestMapper::dtoToListResponse).toList()
        );
    }
}
