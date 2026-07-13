package com.itradingsolutions.itex.api.ip.po.controller;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.common.controller.CommonController;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import com.itradingsolutions.itex.api.ip.po.models.enums.PurchaseOrderHistoryAction;
import com.itradingsolutions.itex.api.ip.po.models.mapper.PurchaseOrderMapper;
import com.itradingsolutions.itex.api.ip.po.models.request.CreatePurchaseOrderRequest;
import com.itradingsolutions.itex.api.ip.po.models.response.OpenLockPurchaseOrderResponse;
import com.itradingsolutions.itex.api.ip.po.service.IPurchaseOrderHistoryService;
import com.itradingsolutions.itex.api.ip.po.service.IPurchaseOrderService;
import com.itradingsolutions.itex.config.security.auth.AccessToAction;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ip/po")
@Validated
@AllArgsConstructor
public class PurchaseOrderController extends CommonController {

    private final IPurchaseOrderService purchaseOrderService;
    private final IPurchaseOrderHistoryService purchaseOrderHistoryService;
    private final PurchaseOrderMapper poMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.CREATE_PURCHASE_ORDER)
    public ResponseEntity<MessageResponse<OpenLockPurchaseOrderResponse>> createPurchaseOrder(
            @RequestBody @Valid CreatePurchaseOrderRequest request
    ) {
        var dto = purchaseOrderService.createPurchaseOrder(request);
        purchaseOrderHistoryService.addHistory(PurchaseOrderHistoryAction.CREATE, null, dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new MessageResponse<>(
                        SUCCESS_TITLE,
                        simpleMessage("ip.po.created"),
                        new OpenLockPurchaseOrderResponse(
                                poMapper.dtoToResponse(dto),
                                true
                        )
                ));
    }
}
