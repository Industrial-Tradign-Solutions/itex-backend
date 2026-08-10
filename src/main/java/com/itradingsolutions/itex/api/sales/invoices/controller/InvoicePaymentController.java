package com.itradingsolutions.itex.api.sales.invoices.controller;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.common.controller.CommonController;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import com.itradingsolutions.itex.api.sales.invoices.models.mapper.InvoicePaymentMapper;
import com.itradingsolutions.itex.api.sales.invoices.models.request.RegisterInvoicePaymentRequest;
import com.itradingsolutions.itex.api.sales.invoices.models.request.VoidInvoicePaymentRequest;
import com.itradingsolutions.itex.api.sales.invoices.models.response.InvoicePaymentResponse;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoicePaymentService;
import com.itradingsolutions.itex.config.security.auth.AccessToAction;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sales/invoice/{invoice_id}/payment")
@Validated
@AllArgsConstructor
public class InvoicePaymentController extends CommonController {

    private final IInvoicePaymentService paymentService;
    private final InvoicePaymentMapper paymentMapper;

    /** Multipart: the {@code payment} part carries the JSON body, {@code receipt} the proof file. */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.REGISTER_PAYMENT_INVOICE)
    public ResponseEntity<MessageResponse<InvoicePaymentResponse>> registerPayment(
            @PathVariable("invoice_id") UUID invoiceId,
            @RequestPart("payment") @Valid RegisterInvoicePaymentRequest request,
            @RequestPart("receipt") MultipartFile receipt
    ) {
        var dto = paymentService.register(invoiceId, request, receipt);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("sales.invoice.payment.registered"),
                paymentMapper.dtoToResponse(dto)
        ));
    }

    @PatchMapping("/{payment_id}/void")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.VOID_PAYMENT_INVOICE)
    public ResponseEntity<MessageResponse<InvoicePaymentResponse>> voidPayment(
            @PathVariable("invoice_id") UUID invoiceId,
            @PathVariable("payment_id") UUID paymentId,
            @RequestBody @Valid VoidInvoicePaymentRequest request
    ) {
        var dto = paymentService.voidPayment(invoiceId, paymentId, request);
        return ResponseEntity.ok(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("sales.invoice.payment.voided"),
                paymentMapper.dtoToResponse(dto)
        ));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.VIEW_INVOICE)
    public ResponseEntity<List<InvoicePaymentResponse>> listPayments(@PathVariable("invoice_id") UUID invoiceId) {
        var payments = paymentService.listByInvoice(invoiceId);
        return ResponseEntity.ok(payments.stream().map(paymentMapper::dtoToResponse).toList());
    }
}
