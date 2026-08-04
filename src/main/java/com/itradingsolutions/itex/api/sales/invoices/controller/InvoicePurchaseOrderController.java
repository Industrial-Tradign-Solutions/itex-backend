package com.itradingsolutions.itex.api.sales.invoices.controller;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.common.controller.CommonController;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import com.itradingsolutions.itex.api.ip.po.models.response.BasicIpPurchaseOrderResponse;
import com.itradingsolutions.itex.api.sales.invoices.models.mapper.InvoiceMapper;
import com.itradingsolutions.itex.api.sales.invoices.models.request.LinkInvoicePurchaseOrdersRequest;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceLinkedPoService;
import com.itradingsolutions.itex.config.security.auth.AccessToAction;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sales/invoice/{invoice_id}/purchase-order")
@Validated
@AllArgsConstructor
public class InvoicePurchaseOrderController extends CommonController {

    private final IInvoiceLinkedPoService linkedPoService;
    private final InvoiceMapper invoiceMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.UPDATE_INVOICE)
    public ResponseEntity<MessageResponse<List<BasicIpPurchaseOrderResponse>>> linkPurchaseOrders(
            @PathVariable("invoice_id") UUID invoiceId,
            @RequestBody @Valid LinkInvoicePurchaseOrdersRequest request
    ) {
        var list = linkedPoService.link(invoiceId, request).stream()
                .map(invoiceMapper::toLinkedPoResponse)
                .toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("sales.invoice.po.linked"),
                list
        ));
    }

    @DeleteMapping("/{ip_po_id}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_INVOICE)
    public ResponseEntity<MessageResponse<UUID>> unlinkPurchaseOrder(
            @PathVariable("invoice_id") UUID invoiceId,
            @PathVariable("ip_po_id") UUID ipPoId
    ) {
        linkedPoService.unlink(invoiceId, ipPoId);
        return ResponseEntity.ok(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("sales.invoice.po.unlinked"),
                ipPoId
        ));
    }
}
