package com.itradingsolutions.itex.api.sales.invoices.controller;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.common.controller.CommonController;
import com.itradingsolutions.itex.api.sales.invoices.models.mapper.InvoiceHistoryMapper;
import com.itradingsolutions.itex.api.sales.invoices.models.response.InvoiceHistoryResponse;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceHistoryService;
import com.itradingsolutions.itex.config.security.auth.AccessToAction;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sales/invoice/{invoiceId}/history")
@AllArgsConstructor
public class InvoiceHistoryController extends CommonController {

    private final IInvoiceHistoryService invoiceHistoryService;
    private final InvoiceHistoryMapper invoiceHistoryMapper;

    @GetMapping
    @AccessToAction(action = ModuleAction.VIEW_HISTORY_INVOICE)
    public ResponseEntity<List<InvoiceHistoryResponse>> getHistory(@PathVariable UUID invoiceId) {
        var resp = invoiceHistoryService.getHistoryById(invoiceId);
        return ResponseEntity.ok(resp.stream().map(invoiceHistoryMapper::dtoToResponse).toList());
    }
}
