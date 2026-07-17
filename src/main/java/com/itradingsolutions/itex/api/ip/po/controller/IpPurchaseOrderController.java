package com.itradingsolutions.itex.api.ip.po.controller;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleOption;
import com.itradingsolutions.itex.api.common.controller.CommonController;
import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;
import com.itradingsolutions.itex.api.common.models.enums.OpenAndLockType;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import com.itradingsolutions.itex.api.ip.po.models.dto.IpPurchaseOrderDTO;
import com.itradingsolutions.itex.api.ip.po.models.enums.IpPurchaseOrderHistoryAction;
import com.itradingsolutions.itex.api.ip.po.models.enums.IpPurchaseOrderStatus;
import com.itradingsolutions.itex.api.ip.po.models.filters.FilterListIpPurchaseOrder;
import com.itradingsolutions.itex.api.ip.po.models.mapper.IpPurchaseOrderHistoryMapper;
import com.itradingsolutions.itex.api.ip.po.models.mapper.IpPurchaseOrderMapper;
import com.itradingsolutions.itex.api.ip.po.models.request.ChangeIpPurchaseOrderQuotationRequest;
import com.itradingsolutions.itex.api.ip.po.models.request.CreateIpPurchaseOrderRequest;
import com.itradingsolutions.itex.api.ip.po.models.request.UpdateIpPurchaseOrderRequest;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @PutMapping("/{po_id}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_PURCHASE_ORDER)
    public ResponseEntity<MessageResponse<IpPurchaseOrderResponse>> updateIpPurchaseOrder(
            @PathVariable("po_id") UUID poId,
            @RequestBody @Valid UpdateIpPurchaseOrderRequest request
    ) {
        var oldPo = purchaseOrderService.findById(poId);
        var resp = purchaseOrderService.updateIpPurchaseOrder(poId, request);
        purchaseOrderHistoryService.addHistory(IpPurchaseOrderHistoryAction.UPDATE, oldPo, resp);
        logRemovedProducts(oldPo, resp, poId);
        return ResponseEntity.ok(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("ip.po.updated"),
                poMapper.dtoToResponse(resp)
        ));
    }

    @DeleteMapping("/{po_id}/quotation")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_PURCHASE_ORDER)
    public ResponseEntity<MessageResponse<IpPurchaseOrderResponse>> removeQuotationFromPurchaseOrder(
            @PathVariable("po_id") UUID poId
    ) {
        var oldPo = purchaseOrderService.findById(poId);
        var resp = purchaseOrderService.removeQuotationFromPurchaseOrder(poId);
        purchaseOrderHistoryService.addHistory(IpPurchaseOrderHistoryAction.REMOVE_QUOTATION, oldPo, resp);
        logPurgedQuotationAssociations(oldPo, resp, poId);
        return ResponseEntity.ok(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("ip.po.quotation.removed"),
                poMapper.dtoToResponse(resp)
        ));
    }

    @PatchMapping("/{po_id}/quotation")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_PURCHASE_ORDER)
    public ResponseEntity<MessageResponse<IpPurchaseOrderResponse>> changeQuotationOfPurchaseOrder(
            @PathVariable("po_id") UUID poId,
            @RequestBody @Valid ChangeIpPurchaseOrderQuotationRequest request
    ) {
        var oldPo = purchaseOrderService.findById(poId);
        var resp = purchaseOrderService.changeQuotationOfPurchaseOrder(poId, request.quotationId());
        purchaseOrderHistoryService.addHistory(IpPurchaseOrderHistoryAction.CHANGE_QUOTATION, oldPo, resp);
        logPurgedQuotationAssociations(oldPo, resp, poId);
        return ResponseEntity.ok(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("ip.po.quotation.changed"),
                poMapper.dtoToResponse(resp)
        ));
    }

    private void logRemovedProducts(IpPurchaseOrderDTO oldPo, IpPurchaseOrderDTO newPo, UUID poId) {
        var remainingIds = Optional.ofNullable(newPo.getProducts()).orElseGet(List::of).stream()
                .map(BaseDTO::getId)
                .collect(Collectors.toSet());
        Optional.ofNullable(oldPo.getProducts()).orElseGet(List::of).stream()
                .filter(product -> !remainingIds.contains(product.getId()))
                .forEach(product -> purchaseOrderHistoryService.addHistoryProduct(
                        IpPurchaseOrderHistoryAction.REMOVE_PRODUCT, product, null, poId));
    }

    private void logPurgedQuotationAssociations(IpPurchaseOrderDTO oldPo, IpPurchaseOrderDTO newPo, UUID poId) {
        logRemovedProducts(oldPo, newPo, poId);

        var remainingQChargeIds = Optional.ofNullable(newPo.getImportedQuotationCharges()).orElseGet(List::of).stream()
                .map(BaseDTO::getId)
                .collect(Collectors.toSet());
        Optional.ofNullable(oldPo.getImportedQuotationCharges()).orElseGet(List::of).stream()
                .filter(charge -> !remainingQChargeIds.contains(charge.getId()))
                .forEach(charge -> purchaseOrderHistoryService.addHistoryImportedQCharge(
                        IpPurchaseOrderHistoryAction.REMOVE_IMPORTED_Q_CHARGE, charge, null, poId));

        var remainingQrChargeIds = Optional.ofNullable(newPo.getImportedQuoteRequestCharges()).orElseGet(List::of).stream()
                .map(BaseDTO::getId)
                .collect(Collectors.toSet());
        Optional.ofNullable(oldPo.getImportedQuoteRequestCharges()).orElseGet(List::of).stream()
                .filter(charge -> !remainingQrChargeIds.contains(charge.getId()))
                .forEach(charge -> purchaseOrderHistoryService.addHistoryImportedQrCharge(
                        IpPurchaseOrderHistoryAction.REMOVE_IMPORTED_QR_CHARGE, charge, null, poId));
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

    @PatchMapping("/{po_id}/change-status")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.IP_PURCHASE_ORDERS)
    public ResponseEntity<MessageResponse<IpPurchaseOrderResponse>> changeStatusIpPurchaseOrder(
            @PathVariable("po_id") UUID poId,
            @RequestParam IpPurchaseOrderStatus status
    ) {
        if (status.equals(IpPurchaseOrderStatus.REJECTED))
            throw new IllegalArgumentException("Cannot change status to REJECTED");
        var oldPo = purchaseOrderService.findById(poId);
        var resp = purchaseOrderService.changeStatusIpPurchaseOrder(poId, status);
        purchaseOrderHistoryService.addHistory(IpPurchaseOrderHistoryAction.STATUS_CHANGE, oldPo, resp);
        return ResponseEntity.ok(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("ip.po.change-status"),
                poMapper.dtoToResponse(resp)
        ));
    }

    @DeleteMapping("/{po_id}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.REJECT_PURCHASE_ORDER)
    public ResponseEntity<MessageResponse<IpPurchaseOrderResponse>> rejectIpPurchaseOrder(
            @PathVariable("po_id") UUID poId
    ) {
        var oldPo = purchaseOrderService.findById(poId);
        var resp = purchaseOrderService.rejectIpPurchaseOrder(poId);
        purchaseOrderHistoryService.addHistory(IpPurchaseOrderHistoryAction.REJECTED, oldPo, resp);
        return ResponseEntity.ok(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("ip.po.rejected"),
                poMapper.dtoToResponse(resp)
        ));
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
        var ids = list.stream().map(BaseDTO::getId).toList();
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
