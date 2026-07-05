package com.itradingsolutions.itex.api.ip.q.controller;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.common.controller.CommonController;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationProductDTO;
import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationHistoryAction;
import com.itradingsolutions.itex.api.ip.q.models.mapper.IpQuotationProductMapper;
import com.itradingsolutions.itex.api.ip.q.models.requests.IpQuotationProductBulkRequest;
import com.itradingsolutions.itex.api.ip.q.models.requests.IpQuotationProductRequest;
import com.itradingsolutions.itex.api.ip.q.models.response.IpQuotationProductResponse;
import com.itradingsolutions.itex.api.ip.q.service.IIpQuotationHistoryService;
import com.itradingsolutions.itex.api.ip.q.service.IIpQuotationProductService;
import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestProductDTO;
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
@RequestMapping("/ip/q/{id_quotation}/product")
@Validated
@AllArgsConstructor
public class IpQuotationProductController extends CommonController {

    private final IIpQuotationHistoryService qHistoryService;
    private final IIpQuotationProductService qProductService;
    private final IpQuotationProductMapper qProductMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.UPDATE_IP_QUOTATIONS)
    public ResponseEntity<MessageResponse<List<IpQuotationProductResponse>>> addProductToQuotation(
            @PathVariable(name = "id_quotation") UUID idQuotation,
            @RequestBody @Valid IpQuotationProductBulkRequest bulkRequest
    ) {
        var dtos = bulkRequest.products().stream()
                .map(this::buildDTO)
                .toList();

        var created = qProductService.createIpQuotationProducts(dtos, idQuotation);

        created.forEach(dto ->
                qHistoryService.addHistoryProduct(IpQuotationHistoryAction.ADD_PRODUCT, null, dto, idQuotation)
        );

        var responses = created.stream()
                .map(qProductMapper::dtoToResponse)
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("ip.q.product.bulk.created"),
                responses
        ));
    }

    @PutMapping("/{id_q_product}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_IP_QUOTATIONS)
    public ResponseEntity<MessageResponse<IpQuotationProductResponse>> editProductToQuotation(
            @PathVariable(name = "id_quotation") UUID idQuotation,
            @PathVariable(name = "id_q_product") UUID idQProduct,
            @RequestBody @Valid IpQuotationProductRequest request
    ) {
        var oldProduct = qProductService.getIpQuotationProduct(idQProduct, idQuotation);
        var dto = buildDTO(request);
        var resp = qProductService.updateIpQuotationProduct(dto, idQProduct, idQuotation);
        qHistoryService.addHistoryProduct(IpQuotationHistoryAction.UPDATE_PRODUCT, oldProduct, resp, idQuotation);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("ip.q.product.updated"),
                qProductMapper.dtoToResponse(resp)
        ));
    }

    @GetMapping("/{id_q_product}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_IP_QUOTATIONS)
    public ResponseEntity<IpQuotationProductResponse> getProductToQuotation(
            @PathVariable(name = "id_quotation") UUID idQuotation,
            @PathVariable(name = "id_q_product") UUID idQProduct
    ) {
        var qProduct = qProductService.getIpQuotationProduct(idQProduct, idQuotation);
        return ResponseEntity.status(HttpStatus.OK).body(qProductMapper.dtoToResponse(qProduct));
    }

    @DeleteMapping("/{id_q_product}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_IP_QUOTATIONS)
    public ResponseEntity<MessageResponse<UUID>> removeProductToQuotation(
            @PathVariable(name = "id_quotation") UUID idQuotation,
            @PathVariable(name = "id_q_product") UUID idQProduct
    ) {
        var qProduct = qProductService.getIpQuotationProduct(idQProduct, idQuotation);
        qProductService.removeIpQuotationProduct(idQProduct, idQuotation);
        qHistoryService.addHistoryProduct(IpQuotationHistoryAction.REMOVE_PRODUCT, qProduct, null, idQuotation);
        return ResponseEntity.status(HttpStatus.OK).body(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("ip.q.product.removed"),
                idQProduct
        ));
    }

    private IpQuotationProductDTO buildDTO(IpQuotationProductRequest request) {
        var dto = new IpQuotationProductDTO();
        dto.setQuotationsQuoteRequestId(request.quotationsQuoteRequestId());
        dto.setProfitMargin(request.profitMargin());
        dto.setCondition(request.condition());
        if (request.quoteRequestProductId() != null) {
            var qrProductDTO = new IpQuoteRequestProductDTO();
            qrProductDTO.setId(request.quoteRequestProductId());
            dto.setQuoteRequestProduct(qrProductDTO);
        }
        return dto;
    }
}

