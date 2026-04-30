package com.itradingsolutions.itex.api.ip.q.controller;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.common.controller.CommonController;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.ip.qr.models.mappers.IpQuoteRequestMapper;
import com.itradingsolutions.itex.api.ip.qr.models.responses.ListIpQuoteRequestResponse;
import com.itradingsolutions.itex.api.ip.qr.service.IIpQuoteRequestService;
import com.itradingsolutions.itex.config.security.auth.AccessToAction;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ip/qr-q")
@Validated
@AllArgsConstructor
public class IpQuotationQuteRequestController extends CommonController {

    private final IIpQuoteRequestService quoteRequestService;
    private final IpQuoteRequestMapper quoteRequestMapper;

    @GetMapping("/list-qr-by-client-available-quotation/{id-client}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.CREATE_IP_QUOTATIONS)
    public ResponseEntity<List<ListIpQuoteRequestResponse>> getListQuoteRequestByClientAvailableToQuotation(
            @PathVariable(name = "id-client") UUID idClient,
            @RequestParam(name = "view-completed-qr", defaultValue = "false") boolean viewCompletedQR,
            @RequestParam(name = "currency", defaultValue = "USD") Currency currency
    ) {
        var listQR = quoteRequestService.getListQuoteRequestByClientAvailableToQuotation(idClient, viewCompletedQR, currency);
        return ResponseEntity.status(HttpStatus.OK).body(
            listQR.stream().map(quoteRequestMapper::dtoToListResponse).toList()
        );
    }
}
