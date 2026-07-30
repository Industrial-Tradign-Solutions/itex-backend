package com.itradingsolutions.itex.api.sales.invoices.controller;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleOption;
import com.itradingsolutions.itex.api.common.controller.CommonController;
import com.itradingsolutions.itex.api.common.models.enums.OpenAndLockType;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import com.itradingsolutions.itex.api.sales.invoices.models.filters.FilterListInvoice;
import com.itradingsolutions.itex.api.sales.invoices.models.mapper.InvoiceMapper;
import com.itradingsolutions.itex.api.sales.invoices.models.response.ListInvoiceResponse;
import com.itradingsolutions.itex.api.sales.invoices.models.response.OpenLockInvoiceResponse;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceCloneService;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceLockService;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceQueryService;
import com.itradingsolutions.itex.config.security.auth.AccessToAction;
import com.itradingsolutions.itex.config.security.auth.AccessToModule;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/sales/invoice")
@AllArgsConstructor
public class InvoiceController extends CommonController {

    private final IInvoiceQueryService invoiceQueryService;
    private final IInvoiceLockService invoiceLockService;
    private final IInvoiceCloneService invoiceCloneService;
    private final InvoiceMapper invoiceMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.VIEW_INVOICE)
    public ResponseEntity<Page<ListInvoiceResponse>> listAllInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @ModelAttribute FilterListInvoice filters
    ) {
        var resp = invoiceQueryService.listAll(filters.getPageRequest(page, size), filters);
        return ResponseEntity.ok(resp.map(invoiceMapper::dtoToListResponse));
    }

    @GetMapping("/load-open")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.INVOICES)
    public ResponseEntity<List<ListInvoiceResponse>> loadOpenInvoices() {
        var list = invoiceQueryService.listAllOpenByUser(getUserAuthenticated());
        return ResponseEntity.ok(list.stream().map(invoiceMapper::dtoToListResponse).toList());
    }

    @PatchMapping("/close-list")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.INVOICES)
    public ResponseEntity<MessageResponse<List<UUID>>> closeListInvoices() {
        var ids = invoiceLockService.closeAllOpenByUser(getUserAuthenticated());
        return ResponseEntity.ok(new MessageResponse<>(SUCCESS_TITLE, simpleMessage("sales.invoice.all-closed"), ids));
    }

    @PatchMapping("/open-lock/{invoice_id}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.INVOICES)
    public ResponseEntity<OpenLockInvoiceResponse> openLockInvoice(
            @PathVariable("invoice_id") UUID invoiceId,
            @RequestParam OpenAndLockType type
    ) {
        var result = invoiceLockService.openAndLock(invoiceId, type);
        return ResponseEntity.ok(new OpenLockInvoiceResponse(invoiceMapper.dtoToResponse(result.invoice()), result.isValidOpen()));
    }

    @PatchMapping("/close/{invoice_id}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.INVOICES)
    public ResponseEntity<MessageResponse<UUID>> closeInvoice(@PathVariable("invoice_id") UUID invoiceId) {
        invoiceLockService.unlock(invoiceId);
        return ResponseEntity.ok(new MessageResponse<>(SUCCESS_TITLE, simpleMessage("sales.invoice.closed"), invoiceId));
    }

    @PatchMapping("/clone/{invoice_id}")
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.CLONE_INVOICE)
    public ResponseEntity<MessageResponse<ListInvoiceResponse>> cloneInvoice(@PathVariable("invoice_id") UUID invoiceId) {
        var resp = invoiceCloneService.clone(invoiceId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new MessageResponse<>(
                        SUCCESS_TITLE,
                        simpleMessage("sales.invoice.cloned"),
                        invoiceMapper.dtoToListResponse(resp)
                ));
    }
}
