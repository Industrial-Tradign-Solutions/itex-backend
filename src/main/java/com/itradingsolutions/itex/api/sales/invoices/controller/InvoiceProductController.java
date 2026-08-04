package com.itradingsolutions.itex.api.sales.invoices.controller;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.common.controller.CommonController;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import com.itradingsolutions.itex.api.sales.invoices.models.mapper.InvoiceMapper;
import com.itradingsolutions.itex.api.sales.invoices.models.request.ImportInvoiceProductsRequest;
import com.itradingsolutions.itex.api.sales.invoices.models.request.InvoiceProductRequest;
import com.itradingsolutions.itex.api.sales.invoices.models.response.AvailablePoProductResponse;
import com.itradingsolutions.itex.api.sales.invoices.models.response.InvoiceProductResponse;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceProductService;
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
@RequestMapping("/sales/invoice/{invoice_id}/product")
@Validated
@AllArgsConstructor
public class InvoiceProductController extends CommonController {

    private final IInvoiceProductService productService;
    private final InvoiceMapper invoiceMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.UPDATE_INVOICE)
    public ResponseEntity<MessageResponse<InvoiceProductResponse>> addProduct(
            @PathVariable("invoice_id") UUID invoiceId,
            @RequestBody @Valid InvoiceProductRequest request
    ) {
        var dto = productService.add(invoiceId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("sales.invoice.product.added"),
                invoiceMapper.toProductResponse(dto)
        ));
    }

    @PutMapping("/{product_id}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_INVOICE)
    public ResponseEntity<MessageResponse<InvoiceProductResponse>> updateProduct(
            @PathVariable("invoice_id") UUID invoiceId,
            @PathVariable("product_id") UUID productId,
            @RequestBody @Valid InvoiceProductRequest request
    ) {
        var dto = productService.update(invoiceId, productId, request);
        return ResponseEntity.ok(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("sales.invoice.product.updated"),
                invoiceMapper.toProductResponse(dto)
        ));
    }

    @GetMapping("/available-from-pos")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.VIEW_INVOICE)
    public ResponseEntity<List<AvailablePoProductResponse>> listAvailableFromPos(
            @PathVariable("invoice_id") UUID invoiceId
    ) {
        return ResponseEntity.ok(productService.listAvailableFromPos(invoiceId));
    }

    @GetMapping("/{product_id}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.VIEW_INVOICE)
    public ResponseEntity<InvoiceProductResponse> getProduct(
            @PathVariable("invoice_id") UUID invoiceId,
            @PathVariable("product_id") UUID productId
    ) {
        var dto = productService.get(invoiceId, productId);
        return ResponseEntity.ok(invoiceMapper.toProductResponse(dto));
    }

    @DeleteMapping("/{product_id}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_INVOICE)
    public ResponseEntity<MessageResponse<UUID>> removeProduct(
            @PathVariable("invoice_id") UUID invoiceId,
            @PathVariable("product_id") UUID productId
    ) {
        productService.remove(invoiceId, productId);
        return ResponseEntity.ok(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("sales.invoice.product.removed"),
                productId
        ));
    }

    @PostMapping("/import-from-po")
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.UPDATE_INVOICE)
    public ResponseEntity<MessageResponse<List<InvoiceProductResponse>>> importFromPo(
            @PathVariable("invoice_id") UUID invoiceId,
            @RequestBody @Valid ImportInvoiceProductsRequest request
    ) {
        var list = productService.importFromPo(invoiceId, request).stream()
                .map(invoiceMapper::toProductResponse)
                .toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("sales.invoice.product.imported"),
                list
        ));
    }
}
