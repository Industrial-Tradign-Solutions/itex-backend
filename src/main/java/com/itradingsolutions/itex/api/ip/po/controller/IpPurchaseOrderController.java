package com.itradingsolutions.itex.api.ip.po.controller;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleOption;
import com.itradingsolutions.itex.api.common.controller.CommonController;
import com.itradingsolutions.itex.api.common.models.enums.OpenAndLockType;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import com.itradingsolutions.itex.api.ip.po.models.enums.IpPurchaseOrderHistoryAction;
import com.itradingsolutions.itex.api.ip.po.models.filters.FilterListIpPurchaseOrder;
import com.itradingsolutions.itex.api.ip.po.models.mapper.IpPurchaseOrderHistoryMapper;
import com.itradingsolutions.itex.api.ip.po.models.mapper.IpPurchaseOrderMapper;
import com.itradingsolutions.itex.api.ip.po.models.request.CreateIpPurchaseOrderRequest;
import com.itradingsolutions.itex.api.ip.po.models.response.ListIpPurchaseOrderResponse;
import com.itradingsolutions.itex.api.ip.po.models.response.OpenLockIpPurchaseOrderResponse;
import com.itradingsolutions.itex.api.ip.po.models.response.IpPurchaseOrderHistoryResponse;
import com.itradingsolutions.itex.api.ip.po.models.response.IpPurchaseOrderResponse;
import com.itradingsolutions.itex.api.ip.po.service.IIpPurchaseOrderHistoryService;
import com.itradingsolutions.itex.api.ip.po.service.IIpPurchaseOrderService;
import com.itradingsolutions.itex.config.security.auth.AccessToAction;
import com.itradingsolutions.itex.config.security.auth.AccessToModule;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ip/po")
@Validated
@AllArgsConstructor
public class IpPurchaseOrderController extends CommonController {

    private final IIpPurchaseOrderService purchaseOrderService;
    private final IIpPurchaseOrderHistoryService purchaseOrderHistoryService;
    private final IpPurchaseOrderMapper poMapper;
    private final IpPurchaseOrderHistoryMapper poHistoryMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.CREATE_PURCHASE_ORDER)
    public ResponseEntity<MessageResponse<OpenLockIpPurchaseOrderResponse>> createIpPurchaseOrder(
            @RequestBody @Valid CreateIpPurchaseOrderRequest request
    ) {
        var dto = purchaseOrderService.createIpPurchaseOrder(request);
        purchaseOrderHistoryService.addHistory(IpPurchaseOrderHistoryAction.CREATE, null, dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new MessageResponse<>(
                        SUCCESS_TITLE,
                        simpleMessage("ip.po.created"),
                        new OpenLockIpPurchaseOrderResponse(poMapper.dtoToResponse(dto), true)
                ));
    }

    @GetMapping("/history/{po_id}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.VIEW_HISTORY_PURCHASE_ORDER)
    public ResponseEntity<List<IpPurchaseOrderHistoryResponse>> getHistory(
            @PathVariable("po_id") UUID poId
    ) {
        var resp = purchaseOrderHistoryService.getHistoryById(poId);
        return ResponseEntity.ok(
                resp.stream().map(poHistoryMapper::dtoToResponse).toList()
        );
    }

    @PatchMapping("/clone/{po_id}")
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.CLONE_PURCHASE_ORDER)
    public ResponseEntity<MessageResponse<IpPurchaseOrderResponse>> cloneIpPurchaseOrder(
            @PathVariable("po_id") UUID poId
    ) {
        var original = purchaseOrderService.findById(poId);
        var resp = purchaseOrderService.cloneIpPurchaseOrder(poId);
        purchaseOrderHistoryService.addHistory(IpPurchaseOrderHistoryAction.CLONE, original, resp);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new MessageResponse<>(
                        SUCCESS_TITLE,
                        simpleMessage("ip.po.cloned"),
                        poMapper.dtoToResponse(resp)
                ));
    }

    @PatchMapping("/open-lock/{po_id}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.IP_PURCHASE_ORDERS)
    public ResponseEntity<OpenLockIpPurchaseOrderResponse> openLockIpPurchaseOrder(
            @PathVariable("po_id") UUID poId,
            @RequestParam OpenAndLockType type
    ) {
        var po = purchaseOrderService.openAndLockIpPurchaseOrder(poId, type);
        return ResponseEntity.status(HttpStatus.OK).body(
                new OpenLockIpPurchaseOrderResponse(
                        poMapper.dtoToResponse(po),
                        isOpenByUsername(po.getOpenBy(), type)
                )
        );
    }

    @PatchMapping("/close/{po_id}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.IP_PURCHASE_ORDERS)
    public ResponseEntity<MessageResponse<UUID>> closeIpPurchaseOrder(
            @PathVariable("po_id") UUID poId
    ) {
        purchaseOrderService.unlockIpPurchaseOrder(poId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new MessageResponse<>(SUCCESS_TITLE, simpleMessage("ip.po.closed"), poId));
    }

    @GetMapping("/load-open")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.IP_PURCHASE_ORDERS)
    public ResponseEntity<List<IpPurchaseOrderResponse>> loadOpenIpPurchaseOrders() {
        var list = purchaseOrderService.listAllOpenByUser(getUserAuthenticated());
        return ResponseEntity.ok(
                list.stream().map(poMapper::dtoToResponse).toList()
        );
    }

    @PatchMapping("/close-list")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.IP_PURCHASE_ORDERS)
    public ResponseEntity<MessageResponse<List<UUID>>> closeListIpPurchaseOrders() {
        var list = purchaseOrderService.listAllOpenByUser(getUserAuthenticated());
        var ids = list.stream().map(item -> item.getId()).toList();
        purchaseOrderService.batchUnlock(ids);
        return ResponseEntity
                .ok(new MessageResponse<>(SUCCESS_TITLE, simpleMessage("ip.po.all-closed"), ids));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.IP_PURCHASE_ORDERS)
    public ResponseEntity<Page<ListIpPurchaseOrderResponse>> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @ModelAttribute FilterListIpPurchaseOrder filters
    ) {
        var resp = purchaseOrderService.listAll(filters.getPageRequest(page, size), filters);
        var list = resp.getContent().stream()
                .map(poMapper::dtoToListResponse).toList();
        return ResponseEntity.ok(new PageImpl<>(list, resp.getPageable(), resp.getTotalElements()));
    }
}
