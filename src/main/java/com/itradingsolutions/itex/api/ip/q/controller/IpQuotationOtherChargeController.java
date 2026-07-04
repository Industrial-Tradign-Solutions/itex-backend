package com.itradingsolutions.itex.api.ip.q.controller;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.common.controller.CommonController;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationOtherChargeDTO;
import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationHistoryAction;
import com.itradingsolutions.itex.api.ip.q.models.mapper.IpQuotationOtherChargeMapper;
import com.itradingsolutions.itex.api.ip.q.models.mapper.IpQuotationOtherChargesQuoteRequestMapper;
import com.itradingsolutions.itex.api.ip.q.models.request.IpQuotationOtherChargeRequest;
import com.itradingsolutions.itex.api.ip.q.models.request.IpQuotationOtherChargesBulkImportRequest;
import com.itradingsolutions.itex.api.ip.q.models.response.IpQuotationOtherChargeResponse;
import com.itradingsolutions.itex.api.ip.q.models.response.IpQuotationOtherChargesQuoteRequestResponse;
import com.itradingsolutions.itex.api.ip.q.models.response.QuotationAvailableQrOtherChargeResponse;
import com.itradingsolutions.itex.api.ip.q.service.IIpQuotationHistoryService;
import com.itradingsolutions.itex.api.ip.q.service.IIpQuotationOtherChargeService;
import com.itradingsolutions.itex.api.ip.q.service.IIpQuotationOtherChargesQuoteRequestService;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ip/q/{id_quotation}/other_charges")
@Validated
@AllArgsConstructor
public class IpQuotationOtherChargeController extends CommonController {

    private final IIpQuotationHistoryService qHistoryService;
    private final IIpQuotationOtherChargeService otherChargeService;
    private final IpQuotationOtherChargeMapper otherChargeMapper;
    private final IIpQuotationOtherChargesQuoteRequestService qrImportService;
    private final IpQuotationOtherChargesQuoteRequestMapper qrImportMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.UPDATE_IP_QUOTATIONS)
    public ResponseEntity<MessageResponse<IpQuotationOtherChargeResponse>> addOtherChargeToQuotation(
            @PathVariable(name = "id_quotation") UUID idQuotation,
            @RequestBody @Valid IpQuotationOtherChargeRequest request
    ) {
        var dto = otherChargeMapper.requestToDTO(request);
        var resp = otherChargeService.create(dto, idQuotation);
        qHistoryService.addHistoryOtherCharge(IpQuotationHistoryAction.ADD_OTHER_CHARGE, null, resp, idQuotation);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse<>(
                        SUCCESS_TITLE,
                        simpleMessage("ip.q.other-charges.created"),
                        otherChargeMapper.dtoToResponse(resp)
                )
        );
    }

    @PutMapping("/{id_other_charge}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_IP_QUOTATIONS)
    public ResponseEntity<MessageResponse<IpQuotationOtherChargeResponse>> editOtherChargeToQuotation(
            @PathVariable(name = "id_quotation") UUID idQuotation,
            @PathVariable(name = "id_other_charge") UUID idOtherCharge,
            @RequestBody @Valid IpQuotationOtherChargeRequest request
    ) {
        var oldOtherCharge = otherChargeService.get(idOtherCharge, idQuotation);
        var dto = otherChargeMapper.requestToDTO(request);
        var resp = otherChargeService.update(dto, idOtherCharge, idQuotation);
        qHistoryService.addHistoryOtherCharge(IpQuotationHistoryAction.UPDATE_OTHER_CHARGE, oldOtherCharge, resp, idQuotation);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse<>(
                        SUCCESS_TITLE,
                        simpleMessage("ip.q.other-charges.updated"),
                        otherChargeMapper.dtoToResponse(resp)
                )
        );
    }

    @GetMapping("/{id_other_charge}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_IP_QUOTATIONS)
    public ResponseEntity<IpQuotationOtherChargeResponse> getOtherChargeToQuotation(
            @PathVariable(name = "id_quotation") UUID idQuotation,
            @PathVariable(name = "id_other_charge") UUID idOtherCharge
    ) {
        var otherCharge = otherChargeService.get(idOtherCharge, idQuotation);
        return ResponseEntity.status(HttpStatus.OK).body(otherChargeMapper.dtoToResponse(otherCharge));
    }

    @DeleteMapping("/{id_other_charge}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_IP_QUOTATIONS)
    public ResponseEntity<MessageResponse<UUID>> removeOtherChargeToQuotation(
            @PathVariable(name = "id_quotation") UUID idQuotation,
            @PathVariable(name = "id_other_charge") UUID idOtherCharge
    ) {
        var otherCharge = otherChargeService.get(idOtherCharge, idQuotation);
        otherChargeService.remove(idOtherCharge, idQuotation);
        qHistoryService.addHistoryOtherCharge(IpQuotationHistoryAction.REMOVE_OTHER_CHARGE, null, otherCharge, idQuotation);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse<>(
                        SUCCESS_TITLE,
                        simpleMessage("ip.q.other-charges.removed"),
                        idOtherCharge
                )
        );
    }

    @PostMapping("/import-from-qr")
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.UPDATE_IP_QUOTATIONS)
    public ResponseEntity<MessageResponse<List<IpQuotationOtherChargesQuoteRequestResponse>>> bulkImportFromQr(
            @PathVariable(name = "id_quotation") UUID idQuotation,
            @RequestBody @Valid IpQuotationOtherChargesBulkImportRequest request
    ) {
        var imported = qrImportService.bulkImport(request.items(), idQuotation);
        imported.forEach(dto -> {
            var historyDto = new IpQuotationOtherChargeDTO();
            historyDto.setDescription(dto.getQrOtherCharge().getDescription());
            historyDto.setValue(dto.getQrOtherCharge().getValue());
            qHistoryService.addHistoryOtherCharge(IpQuotationHistoryAction.ADD_OTHER_CHARGE, null, historyDto, idQuotation);
        });
        var responses = imported.stream()
                .map(qrImportMapper::dtoToResponse)
                .toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("ip.q.other-charges.imported-from-qr.created"),
                responses
        ));
    }

    @GetMapping("/imported-from-qr/{id}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_IP_QUOTATIONS)
    public ResponseEntity<IpQuotationOtherChargesQuoteRequestResponse> getImportedFromQr(
            @PathVariable(name = "id_quotation") UUID idQuotation,
            @PathVariable(name = "id") UUID id
    ) {
        var dto = qrImportService.get(id, idQuotation);
        return ResponseEntity.status(HttpStatus.OK).body(qrImportMapper.dtoToResponse(dto));
    }

    @DeleteMapping("/imported-from-qr/{id}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_IP_QUOTATIONS)
    public ResponseEntity<MessageResponse<UUID>> removeImportedFromQr(
            @PathVariable(name = "id_quotation") UUID idQuotation,
            @PathVariable(name = "id") UUID id
    ) {
        var dto = qrImportService.get(id, idQuotation);
        qrImportService.remove(id, idQuotation);
        var historyDto = new IpQuotationOtherChargeDTO();
        historyDto.setDescription(dto.getQrOtherCharge().getDescription());
        historyDto.setValue(dto.getQrOtherCharge().getValue());
        qHistoryService.addHistoryOtherCharge(IpQuotationHistoryAction.REMOVE_OTHER_CHARGE, null, historyDto, idQuotation);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("ip.q.other-charges.imported-from-qr.removed"),
                id
        ));
    }

    @GetMapping("/available-from-qr")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_IP_QUOTATIONS)
    public ResponseEntity<List<QuotationAvailableQrOtherChargeResponse>> getAvailableFromQr(
            @PathVariable(name = "id_quotation") UUID idQuotation
    ) {
        var responses = qrImportService.getAvailableQrOtherCharges(idQuotation);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }
}
