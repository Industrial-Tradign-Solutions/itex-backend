package com.itradingsolutions.itex.api.sales.invoices.controller;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.common.controller.CommonController;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import com.itradingsolutions.itex.api.sales.invoices.models.mapper.InvoiceMapper;
import com.itradingsolutions.itex.api.sales.invoices.models.request.InvoiceTaxRequest;
import com.itradingsolutions.itex.api.sales.invoices.models.response.InvoiceTaxResponse;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceTaxService;
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
@RequestMapping("/sales/invoice/{invoice_id}/tax")
@Validated
@AllArgsConstructor
public class InvoiceTaxController extends CommonController {

    private final IInvoiceTaxService taxService;
    private final InvoiceMapper invoiceMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.UPDATE_INVOICE)
    public ResponseEntity<MessageResponse<InvoiceTaxResponse>> addTax(
            @PathVariable("invoice_id") UUID invoiceId,
            @RequestBody @Valid InvoiceTaxRequest request
    ) {
        var dto = taxService.create(invoiceId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("sales.invoice.tax.added"),
                invoiceMapper.toTaxResponse(dto)
        ));
    }

    @PutMapping("/{tax_id}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_INVOICE)
    public ResponseEntity<MessageResponse<InvoiceTaxResponse>> updateTax(
            @PathVariable("invoice_id") UUID invoiceId,
            @PathVariable("tax_id") UUID taxId,
            @RequestBody @Valid InvoiceTaxRequest request
    ) {
        var dto = taxService.update(invoiceId, taxId, request);
        return ResponseEntity.ok(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("sales.invoice.tax.updated"),
                invoiceMapper.toTaxResponse(dto)
        ));
    }

    @GetMapping("/{tax_id}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.VIEW_INVOICE)
    public ResponseEntity<InvoiceTaxResponse> getTax(
            @PathVariable("invoice_id") UUID invoiceId,
            @PathVariable("tax_id") UUID taxId
    ) {
        var dto = taxService.get(invoiceId, taxId);
        return ResponseEntity.ok(invoiceMapper.toTaxResponse(dto));
    }

    @DeleteMapping("/{tax_id}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_INVOICE)
    public ResponseEntity<MessageResponse<UUID>> removeTax(
            @PathVariable("invoice_id") UUID invoiceId,
            @PathVariable("tax_id") UUID taxId
    ) {
        taxService.remove(invoiceId, taxId);
        return ResponseEntity.ok(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("sales.invoice.tax.removed"),
                taxId
        ));
    }
}
