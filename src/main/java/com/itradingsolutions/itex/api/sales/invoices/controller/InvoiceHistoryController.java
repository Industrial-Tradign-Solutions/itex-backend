package com.itradingsolutions.itex.api.sales.invoices.controller;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.common.controller.CommonController;
import com.itradingsolutions.itex.api.sales.invoices.models.mapper.InvoiceHistoryMapper;
import com.itradingsolutions.itex.api.sales.invoices.models.response.InvoiceHistoryResponse;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceHistoryService;
import com.itradingsolutions.itex.config.security.auth.AccessToAction;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/sales/invoice/{invoice_id}/history")
@AllArgsConstructor
public class InvoiceHistoryController extends CommonController {

    private final IInvoiceHistoryService historyService;
    private final InvoiceHistoryMapper historyMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.VIEW_HISTORY_INVOICE)
    public ResponseEntity<List<InvoiceHistoryResponse>> getHistory(@PathVariable("invoice_id") UUID invoiceId) {
        var resp = historyService.getHistoryById(invoiceId);
        return ResponseEntity.ok(resp.stream().map(historyMapper::dtoToResponse).toList());
    }
}
