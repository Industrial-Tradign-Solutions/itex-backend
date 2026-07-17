package com.itradingsolutions.itex.api.ip.po.controller;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.common.controller.CommonController;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import com.itradingsolutions.itex.api.ip.po.models.enums.IpPurchaseOrderHistoryAction;
import com.itradingsolutions.itex.api.ip.po.models.mapper.IpPurchaseOrderProductMapper;
import com.itradingsolutions.itex.api.ip.po.models.request.AddIpPurchaseOrderProductsRequest;
import com.itradingsolutions.itex.api.ip.po.models.response.EligibleIpPurchaseOrderProductResponse;
import com.itradingsolutions.itex.api.ip.po.models.response.IpPurchaseOrderProductResponse;
import com.itradingsolutions.itex.api.ip.po.service.IIpPurchaseOrderHistoryService;
import com.itradingsolutions.itex.api.ip.po.service.IIpPurchaseOrderProductService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ip/po/{id_po}/product")
@Validated
@AllArgsConstructor
public class IpPurchaseOrderProductController extends CommonController {

    private final IIpPurchaseOrderProductService productService;
    private final IIpPurchaseOrderHistoryService historyService;
    private final IpPurchaseOrderProductMapper productMapper;

    @GetMapping("/available")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_PURCHASE_ORDER)
    public ResponseEntity<List<EligibleIpPurchaseOrderProductResponse>> getEligibleProducts(
            @PathVariable("id_po") UUID poId
    ) {
        return ResponseEntity.ok(productService.getEligibleProducts(poId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.UPDATE_PURCHASE_ORDER)
    public ResponseEntity<MessageResponse<List<IpPurchaseOrderProductResponse>>> addProducts(
            @PathVariable("id_po") UUID poId,
            @RequestBody @Valid AddIpPurchaseOrderProductsRequest request
    ) {
        var created = productService.addProducts(poId, request.quotationProductIds());
        created.forEach(dto -> historyService.addHistoryProduct(IpPurchaseOrderHistoryAction.ADD_PRODUCT, null, dto, poId));
        var responses = created.stream().map(productMapper::dtoToResponse).toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("ip.po.product.bulk.created"),
                responses
        ));
    }

    @DeleteMapping("/{id_po_product}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_PURCHASE_ORDER)
    public ResponseEntity<MessageResponse<UUID>> removeProduct(
            @PathVariable("id_po") UUID poId,
            @PathVariable("id_po_product") UUID productId
    ) {
        var oldProduct = productService.get(productId, poId);
        productService.remove(productId, poId);
        historyService.addHistoryProduct(IpPurchaseOrderHistoryAction.REMOVE_PRODUCT, null, oldProduct, poId);
        return ResponseEntity.ok(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("ip.po.product.removed"),
                productId
        ));
    }
}
