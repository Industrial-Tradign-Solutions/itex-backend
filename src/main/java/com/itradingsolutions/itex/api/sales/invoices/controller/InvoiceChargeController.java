package com.itradingsolutions.itex.api.sales.invoices.controller;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.common.controller.CommonController;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import com.itradingsolutions.itex.api.sales.invoices.models.mapper.InvoiceMapper;
import com.itradingsolutions.itex.api.sales.invoices.models.request.ImportInvoiceChargesRequest;
import com.itradingsolutions.itex.api.sales.invoices.models.request.InvoiceChargeRequest;
import com.itradingsolutions.itex.api.sales.invoices.models.response.AvailablePoChargeResponse;
import com.itradingsolutions.itex.api.sales.invoices.models.response.InvoiceChargeResponse;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceChargeService;
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
@RequestMapping("/sales/invoice/{invoice_id}/charge")
@Validated
@AllArgsConstructor
public class InvoiceChargeController extends CommonController {

    private final IInvoiceChargeService chargeService;
    private final InvoiceMapper invoiceMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.UPDATE_INVOICE)
    public ResponseEntity<MessageResponse<InvoiceChargeResponse>> addCharge(
            @PathVariable("invoice_id") UUID invoiceId,
            @RequestBody @Valid InvoiceChargeRequest request
    ) {
        var dto = chargeService.create(invoiceId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("sales.invoice.charge.added"),
                invoiceMapper.toChargeResponse(dto)
        ));
    }

    @PutMapping("/{charge_id}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_INVOICE)
    public ResponseEntity<MessageResponse<InvoiceChargeResponse>> updateCharge(
            @PathVariable("invoice_id") UUID invoiceId,
            @PathVariable("charge_id") UUID chargeId,
            @RequestBody @Valid InvoiceChargeRequest request
    ) {
        var dto = chargeService.update(invoiceId, chargeId, request);
        return ResponseEntity.ok(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("sales.invoice.charge.updated"),
                invoiceMapper.toChargeResponse(dto)
        ));
    }

    @GetMapping("/available-from-pos")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.VIEW_INVOICE)
    public ResponseEntity<List<AvailablePoChargeResponse>> listAvailableFromPos(
            @PathVariable("invoice_id") UUID invoiceId
    ) {
        return ResponseEntity.ok(chargeService.listAvailableFromPos(invoiceId));
    }

    @GetMapping("/{charge_id}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.VIEW_INVOICE)
    public ResponseEntity<InvoiceChargeResponse> getCharge(
            @PathVariable("invoice_id") UUID invoiceId,
            @PathVariable("charge_id") UUID chargeId
    ) {
        var dto = chargeService.get(invoiceId, chargeId);
        return ResponseEntity.ok(invoiceMapper.toChargeResponse(dto));
    }

    @DeleteMapping("/{charge_id}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_INVOICE)
    public ResponseEntity<MessageResponse<UUID>> removeCharge(
            @PathVariable("invoice_id") UUID invoiceId,
            @PathVariable("charge_id") UUID chargeId
    ) {
        chargeService.remove(invoiceId, chargeId);
        return ResponseEntity.ok(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("sales.invoice.charge.removed"),
                chargeId
        ));
    }

    @PostMapping("/import-from-po")
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.UPDATE_INVOICE)
    public ResponseEntity<MessageResponse<List<InvoiceChargeResponse>>> importFromPo(
            @PathVariable("invoice_id") UUID invoiceId,
            @RequestBody @Valid ImportInvoiceChargesRequest request
    ) {
        var list = chargeService.importFromPo(invoiceId, request).stream()
                .map(invoiceMapper::toChargeResponse)
                .toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("sales.invoice.charge.imported"),
                list
        ));
    }
}
