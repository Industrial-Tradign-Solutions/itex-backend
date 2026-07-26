package com.itradingsolutions.itex.api.ip.po.controller;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.common.controller.CommonController;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import com.itradingsolutions.itex.api.ip.po.models.enums.IpPurchaseOrderHistoryAction;
import com.itradingsolutions.itex.api.ip.po.models.mapper.IpPurchaseOrderOtherChargeMapper;
import com.itradingsolutions.itex.api.ip.po.models.mapper.IpPurchaseOrderOtherChargesQuotationMapper;
import com.itradingsolutions.itex.api.ip.po.models.mapper.IpPurchaseOrderOtherChargesQuotationQrMapper;
import com.itradingsolutions.itex.api.ip.po.models.request.ImportIpPurchaseOrderOtherChargesRequest;
import com.itradingsolutions.itex.api.ip.po.models.request.IpPurchaseOrderOtherChargeRequest;
import com.itradingsolutions.itex.api.ip.po.models.response.AvailableIpPurchaseOrderOtherChargeResponse;
import com.itradingsolutions.itex.api.ip.po.models.response.IpPurchaseOrderOtherChargeResponse;
import com.itradingsolutions.itex.api.ip.po.models.response.IpPurchaseOrderOtherChargesQuotationQrResponse;
import com.itradingsolutions.itex.api.ip.po.models.response.IpPurchaseOrderOtherChargesQuotationResponse;
import com.itradingsolutions.itex.api.ip.po.service.IIpPurchaseOrderHistoryService;
import com.itradingsolutions.itex.api.ip.po.service.IIpPurchaseOrderOtherChargeService;
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
@RequestMapping("/ip/po/{id_po}/other_charges")
@Validated
@AllArgsConstructor
public class IpPurchaseOrderOtherChargeController extends CommonController {

    private final IIpPurchaseOrderOtherChargeService otherChargeService;
    private final IIpPurchaseOrderHistoryService historyService;
    private final IpPurchaseOrderOtherChargeMapper otherChargeMapper;
    private final IpPurchaseOrderOtherChargesQuotationMapper importedQMapper;
    private final IpPurchaseOrderOtherChargesQuotationQrMapper importedQrMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.UPDATE_PURCHASE_ORDER)
    public ResponseEntity<MessageResponse<IpPurchaseOrderOtherChargeResponse>> addOtherCharge(
            @PathVariable("id_po") UUID poId,
            @RequestBody @Valid IpPurchaseOrderOtherChargeRequest request
    ) {
        var dto = otherChargeMapper.requestToDTO(request);
        var resp = otherChargeService.create(dto, poId);
        historyService.addHistoryOtherCharge(IpPurchaseOrderHistoryAction.ADD_OTHER_CHARGE, null, resp, poId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse<>(
                SUCCESS_TITLE, simpleMessage("ip.po.other-charges.created"), otherChargeMapper.dtoToResponse(resp)));
    }

    @PutMapping("/{id_other_charge}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_PURCHASE_ORDER)
    public ResponseEntity<MessageResponse<IpPurchaseOrderOtherChargeResponse>> updateOtherCharge(
            @PathVariable("id_po") UUID poId,
            @PathVariable("id_other_charge") UUID otherChargeId,
            @RequestBody @Valid IpPurchaseOrderOtherChargeRequest request
    ) {
        var oldCharge = otherChargeService.get(otherChargeId, poId);
        var dto = otherChargeMapper.requestToDTO(request);
        var resp = otherChargeService.update(dto, otherChargeId, poId);
        historyService.addHistoryOtherCharge(IpPurchaseOrderHistoryAction.UPDATE_OTHER_CHARGE, oldCharge, resp, poId);
        return ResponseEntity.ok(new MessageResponse<>(
                SUCCESS_TITLE, simpleMessage("ip.po.other-charges.updated"), otherChargeMapper.dtoToResponse(resp)));
    }

    @GetMapping("/{id_other_charge}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_PURCHASE_ORDER)
    public ResponseEntity<IpPurchaseOrderOtherChargeResponse> getOtherCharge(
            @PathVariable("id_po") UUID poId,
            @PathVariable("id_other_charge") UUID otherChargeId
    ) {
        return ResponseEntity.ok(otherChargeMapper.dtoToResponse(otherChargeService.get(otherChargeId, poId)));
    }

    @DeleteMapping("/{id_other_charge}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_PURCHASE_ORDER)
    public ResponseEntity<MessageResponse<UUID>> removeOtherCharge(
            @PathVariable("id_po") UUID poId,
            @PathVariable("id_other_charge") UUID otherChargeId
    ) {
        var oldCharge = otherChargeService.get(otherChargeId, poId);
        otherChargeService.remove(otherChargeId, poId);
        historyService.addHistoryOtherCharge(IpPurchaseOrderHistoryAction.REMOVE_OTHER_CHARGE, null, oldCharge, poId);
        return ResponseEntity.ok(new MessageResponse<>(
                SUCCESS_TITLE, simpleMessage("ip.po.other-charges.removed"), otherChargeId));
    }

    @PostMapping("/import-from-quotation")
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.UPDATE_PURCHASE_ORDER)
    public ResponseEntity<MessageResponse<List<IpPurchaseOrderOtherChargesQuotationResponse>>> importFromQuotation(
            @PathVariable("id_po") UUID poId,
            @RequestBody @Valid ImportIpPurchaseOrderOtherChargesRequest request
    ) {
        var imported = otherChargeService.importFromQuotation(request.chargeIds(), poId);
        imported.forEach(dto -> historyService.addHistoryImportedQCharge(
                IpPurchaseOrderHistoryAction.ADD_IMPORTED_Q_CHARGE, null, dto, poId));
        var responses = imported.stream().map(importedQMapper::dtoToResponse).toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse<>(
                SUCCESS_TITLE, simpleMessage("ip.po.other-charges.imported-from-quotation.created"), responses));
    }

    @GetMapping("/imported-from-quotation/{id}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_PURCHASE_ORDER)
    public ResponseEntity<IpPurchaseOrderOtherChargesQuotationResponse> getImportedFromQuotation(
            @PathVariable("id_po") UUID poId,
            @PathVariable("id") UUID id
    ) {
        return ResponseEntity.ok(importedQMapper.dtoToResponse(otherChargeService.getImportedFromQuotation(id, poId)));
    }

    @DeleteMapping("/imported-from-quotation/{id}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_PURCHASE_ORDER)
    public ResponseEntity<MessageResponse<UUID>> removeImportedFromQuotation(
            @PathVariable("id_po") UUID poId,
            @PathVariable("id") UUID id
    ) {
        var dto = otherChargeService.getImportedFromQuotation(id, poId);
        otherChargeService.removeImportedFromQuotation(id, poId);
        historyService.addHistoryImportedQCharge(IpPurchaseOrderHistoryAction.REMOVE_IMPORTED_Q_CHARGE, null, dto, poId);
        return ResponseEntity.ok(new MessageResponse<>(
                SUCCESS_TITLE, simpleMessage("ip.po.other-charges.imported-from-quotation.removed"), id));
    }

    @GetMapping("/available-from-quotation")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_PURCHASE_ORDER)
    public ResponseEntity<List<AvailableIpPurchaseOrderOtherChargeResponse>> getAvailableFromQuotation(
            @PathVariable("id_po") UUID poId
    ) {
        return ResponseEntity.ok(otherChargeService.getAvailableFromQuotation(poId));
    }

    @PostMapping("/import-from-quotation-qr")
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.UPDATE_PURCHASE_ORDER)
    public ResponseEntity<MessageResponse<List<IpPurchaseOrderOtherChargesQuotationQrResponse>>> importFromQuotationQr(
            @PathVariable("id_po") UUID poId,
            @RequestBody @Valid ImportIpPurchaseOrderOtherChargesRequest request
    ) {
        var imported = otherChargeService.importFromQuotationQr(request.chargeIds(), poId);
        imported.forEach(dto -> historyService.addHistoryImportedQrCharge(
                IpPurchaseOrderHistoryAction.ADD_IMPORTED_QR_CHARGE, null, dto, poId));
        var responses = imported.stream().map(importedQrMapper::dtoToResponse).toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse<>(
                SUCCESS_TITLE, simpleMessage("ip.po.other-charges.imported-from-quotation-qr.created"), responses));
    }

    @GetMapping("/imported-from-quotation-qr/{id}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_PURCHASE_ORDER)
    public ResponseEntity<IpPurchaseOrderOtherChargesQuotationQrResponse> getImportedFromQuotationQr(
            @PathVariable("id_po") UUID poId,
            @PathVariable("id") UUID id
    ) {
        return ResponseEntity.ok(importedQrMapper.dtoToResponse(otherChargeService.getImportedFromQuotationQr(id, poId)));
    }

    @DeleteMapping("/imported-from-quotation-qr/{id}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_PURCHASE_ORDER)
    public ResponseEntity<MessageResponse<UUID>> removeImportedFromQuotationQr(
            @PathVariable("id_po") UUID poId,
            @PathVariable("id") UUID id
    ) {
        var dto = otherChargeService.getImportedFromQuotationQr(id, poId);
        otherChargeService.removeImportedFromQuotationQr(id, poId);
        historyService.addHistoryImportedQrCharge(IpPurchaseOrderHistoryAction.REMOVE_IMPORTED_QR_CHARGE, null, dto, poId);
        return ResponseEntity.ok(new MessageResponse<>(
                SUCCESS_TITLE, simpleMessage("ip.po.other-charges.imported-from-quotation-qr.removed"), id));
    }

    @GetMapping("/available-from-quotation-qr")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_PURCHASE_ORDER)
    public ResponseEntity<List<AvailableIpPurchaseOrderOtherChargeResponse>> getAvailableFromQuotationQr(
            @PathVariable("id_po") UUID poId
    ) {
        return ResponseEntity.ok(otherChargeService.getAvailableFromQuotationQr(poId));
    }
}
