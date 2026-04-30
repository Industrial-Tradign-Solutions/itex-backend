package com.itradingsolutions.itex.api.ip.qr.controllers;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.common.controller.CommonController;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import com.itradingsolutions.itex.api.ip.qr.models.enums.IpQuoteRequestHistoryAction;
import com.itradingsolutions.itex.api.ip.qr.models.mappers.IpQuoteRequestProductMapper;
import com.itradingsolutions.itex.api.ip.qr.models.requests.IpQuoteRequestProductRequest;
import com.itradingsolutions.itex.api.ip.qr.models.responses.IpQuoteRequestProductResponse;
import com.itradingsolutions.itex.api.ip.qr.service.IIpQuoteRequestHistoryService;
import com.itradingsolutions.itex.api.ip.qr.service.IIpQuoteRequestProductService;
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

import java.util.UUID;

@RestController
@RequestMapping("/ip/qr/{id_quote_request}/product")
@Validated
@AllArgsConstructor
public class IpQuoteRequestProductController extends CommonController {

    private final IIpQuoteRequestHistoryService qrHistoryService;
    private final IIpQuoteRequestProductService qrProductService;
    private final IpQuoteRequestProductMapper qrProductMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.UPDATE_IP_QUOTE_REQUESTS)
    public ResponseEntity<MessageResponse<IpQuoteRequestProductResponse>> addProductToQuoteRequest(
            @PathVariable(name = "id_quote_request") UUID idQuoteRequest,
            @RequestBody @Valid IpQuoteRequestProductRequest request
    ) {
        var dto = qrProductMapper.requestToProductDTO(request);
        var resp = qrProductService.createIpQuoteRequestProduct(dto, idQuoteRequest);
        qrHistoryService.addHistoryProduct(IpQuoteRequestHistoryAction.ADD_PRODUCT, null, resp, idQuoteRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse<>(
                        SUCCESS_TITLE,
                        simpleMessage("ip.qr.product.created"),
                        qrProductMapper.dtoToResponse(resp)
                )
        );
    }

    @PutMapping("/{id_qr_product}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_IP_QUOTE_REQUESTS)
    public ResponseEntity<MessageResponse<IpQuoteRequestProductResponse>> editProductToQuoteRequest(
            @PathVariable(name = "id_quote_request") UUID idQuoteRequest,
            @PathVariable(name = "id_qr_product") UUID idQrProduct,
            @RequestBody @Valid IpQuoteRequestProductRequest request
    ) {
        var oldProduct = qrProductService.getIpQuoteRequestProduct(idQrProduct, idQuoteRequest);
        var dto = qrProductMapper.requestToProductDTO(request);
        var resp = qrProductService.updateIpQuoteRequestProduct(dto,idQrProduct, idQuoteRequest);
        qrHistoryService.addHistoryProduct(IpQuoteRequestHistoryAction.UPDATE_PRODUCT, oldProduct, resp, idQuoteRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse<>(
                        SUCCESS_TITLE,
                        simpleMessage("ip.qr.product.updated"),
                        qrProductMapper.dtoToResponse(resp)
                )
        );
    }

    @GetMapping("/{id_qr_product}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_IP_QUOTE_REQUESTS)
    public ResponseEntity<IpQuoteRequestProductResponse> getProductToQuoteRequest(
            @PathVariable(name = "id_quote_request") UUID idQuoteRequest,
            @PathVariable(name = "id_qr_product") UUID idQrProduct
    ) {
        var qrProduct = qrProductService.getIpQuoteRequestProduct(idQrProduct, idQuoteRequest);
        return ResponseEntity.status(HttpStatus.OK).body(qrProductMapper.dtoToResponse(qrProduct));
    }

    @DeleteMapping("/{id_qr_product}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_IP_QUOTE_REQUESTS)
    public ResponseEntity<MessageResponse<UUID>> removeProductToQuoteRequest(
            @PathVariable(name = "id_quote_request") UUID idQuoteRequest,
            @PathVariable(name = "id_qr_product") UUID idQrProduct
    ) {
        var qrProduct = qrProductService.getIpQuoteRequestProduct(idQrProduct, idQuoteRequest);
        qrProductService.removeIpQuoteRequestProduct(idQrProduct, idQuoteRequest);
        qrHistoryService.addHistoryProduct(IpQuoteRequestHistoryAction.REMOVE_PRODUCT, null, qrProduct, idQuoteRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse<>(
                        SUCCESS_TITLE,
                        simpleMessage("ip.qr.product.removed"),
                        idQrProduct
                )
        );
    }
}
