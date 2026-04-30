package com.itradingsolutions.itex.api.ip.qr.controllers;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.common.controller.CommonController;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import com.itradingsolutions.itex.api.ip.qr.models.enums.IpQuoteRequestHistoryAction;
import com.itradingsolutions.itex.api.ip.qr.models.mappers.IpQuoteRequestOtherChargeMapper;
import com.itradingsolutions.itex.api.ip.qr.models.requests.IpQuoteRequestOtherChargeRequest;
import com.itradingsolutions.itex.api.ip.qr.models.responses.IpQuoteRequestOtherChargeResponse;
import com.itradingsolutions.itex.api.ip.qr.service.IIpQuoteRequestHistoryService;
import com.itradingsolutions.itex.api.ip.qr.service.IIpQuoteRequestOtherChargeService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/ip/qr/{id_quote_request}/other_charges")
@Validated
@AllArgsConstructor
public class IpQuoteRequestOtherChargesController extends CommonController {

    private final IIpQuoteRequestHistoryService qrHistoryService;
    private final IIpQuoteRequestOtherChargeService qrOtherChargeService;
    private final IpQuoteRequestOtherChargeMapper qrOtherChargeMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.UPDATE_IP_QUOTE_REQUESTS)
    public ResponseEntity<MessageResponse<IpQuoteRequestOtherChargeResponse>> addOtherChargeToQuoteRequest(
            @PathVariable(name = "id_quote_request") UUID idQuoteRequest,
            @RequestBody @Valid IpQuoteRequestOtherChargeRequest request
    ) {
        var dto = qrOtherChargeMapper.requestToDTO(request);
        var resp = qrOtherChargeService.create(dto, idQuoteRequest);
        qrHistoryService.addHistoryOtherCharge(IpQuoteRequestHistoryAction.ADD_OTHER_CHARGE, null, resp, idQuoteRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse<>(
                        SUCCESS_TITLE,
                        simpleMessage("ip.qr.other-charges.created"),
                        qrOtherChargeMapper.dtoToResponse(resp)
                )
        );
    }

    @PutMapping("/{id_qr_other_charge}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_IP_QUOTE_REQUESTS)
    public ResponseEntity<MessageResponse<IpQuoteRequestOtherChargeResponse>> editOtherChargeToQuoteRequest(
            @PathVariable(name = "id_quote_request") UUID idQuoteRequest,
            @PathVariable(name = "id_qr_other_charge") UUID idQrOtherCharge,
            @RequestBody @Valid IpQuoteRequestOtherChargeRequest request
    ) {
        var oldOtherCharge = qrOtherChargeService.get(idQrOtherCharge, idQuoteRequest);
        var dto = qrOtherChargeMapper.requestToDTO(request);
        var resp = qrOtherChargeService.update(dto,idQrOtherCharge, idQuoteRequest);
        qrHistoryService.addHistoryOtherCharge(IpQuoteRequestHistoryAction.UPDATE_OTHER_CHARGE, oldOtherCharge, resp, idQuoteRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse<>(
                        SUCCESS_TITLE,
                        simpleMessage("ip.qr.other-charges.updated"),
                        qrOtherChargeMapper.dtoToResponse(resp)
                )
        );
    }

    @GetMapping("/{id_qr_other_charge}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_IP_QUOTE_REQUESTS)
    public ResponseEntity<IpQuoteRequestOtherChargeResponse> getOtherChargeToQuoteRequest(
            @PathVariable(name = "id_quote_request") UUID idQuoteRequest,
            @PathVariable(name = "id_qr_other_charge") UUID idQrOtherCharge
    ) {
        var qrOtherCharge = qrOtherChargeService.get(idQrOtherCharge, idQuoteRequest);
        return ResponseEntity.status(HttpStatus.OK).body(qrOtherChargeMapper.dtoToResponse(qrOtherCharge));
    }

    @DeleteMapping("/{id_qr_other_charge}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_IP_QUOTE_REQUESTS)
    public ResponseEntity<MessageResponse<UUID>> removeOtherChargeToQuoteRequest(
            @PathVariable(name = "id_quote_request") UUID idQuoteRequest,
            @PathVariable(name = "id_qr_other_charge") UUID idQrOtherCharge
    ) {
        var qrOtherCharge = qrOtherChargeService.get(idQrOtherCharge, idQuoteRequest);
        qrOtherChargeService.remove(idQrOtherCharge, idQuoteRequest);
        qrHistoryService.addHistoryOtherCharge(IpQuoteRequestHistoryAction.REMOVE_OTHER_CHARGE, null, qrOtherCharge, idQuoteRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse<>(
                        SUCCESS_TITLE,
                        simpleMessage("ip.qr.other-charges.removed"),
                    idQrOtherCharge
                )
        );
    }
}
