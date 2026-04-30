package com.itradingsolutions.itex.api.ip.products.controllers;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleOption;
import com.itradingsolutions.itex.api.common.controller.CommonController;
import com.itradingsolutions.itex.api.common.models.enums.OpenAndLockType;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import com.itradingsolutions.itex.api.ip.products.models.enums.IpProductHistoryActions;
import com.itradingsolutions.itex.api.ip.products.models.filter.FilterListIpProducts;
import com.itradingsolutions.itex.api.ip.products.models.mappers.IpProductHistoryMapper;
import com.itradingsolutions.itex.api.ip.products.models.mappers.IpProductMapper;
import com.itradingsolutions.itex.api.ip.products.models.requests.IpImportProductRequest;
import com.itradingsolutions.itex.api.ip.products.models.requests.IpProductOutSurplusRequest;
import com.itradingsolutions.itex.api.ip.products.models.requests.IpProductRequest;
import com.itradingsolutions.itex.api.ip.products.models.requests.IpProductAddSurplusRequest;
import com.itradingsolutions.itex.api.ip.products.models.requests.IpProductsImportRequest;
import com.itradingsolutions.itex.api.ip.products.models.requests.ReplaceIpProductRequest;
import com.itradingsolutions.itex.api.ip.products.models.responses.BasicIpProductResponse;
import com.itradingsolutions.itex.api.ip.products.models.responses.IpProductHistoryResponse;
import com.itradingsolutions.itex.api.ip.products.models.responses.IpProductImportResponse;
import com.itradingsolutions.itex.api.ip.products.models.responses.IpProductResponse;
import com.itradingsolutions.itex.api.ip.products.models.responses.ListIpProductResponse;
import com.itradingsolutions.itex.api.ip.products.models.responses.OpenLockIpProductResponse;
import com.itradingsolutions.itex.api.ip.products.services.IIpProductHistoryService;
import com.itradingsolutions.itex.api.ip.products.services.IIpProductService;
import com.itradingsolutions.itex.config.security.auth.AccessToAction;
import com.itradingsolutions.itex.config.security.auth.AccessToModule;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ip/products")
@Validated
@RequiredArgsConstructor
public class IpProductController extends CommonController {

    private final IIpProductHistoryService productHistoryService;
    private final IpProductHistoryMapper productHistoryMapper;
    private final IpProductMapper productMapper;
    private final IIpProductService productService;

    @GetMapping("/history/{product_id}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.VIEW_HISTORY_IP_PRODUCT)
    public ResponseEntity<List<IpProductHistoryResponse>> getHistory(@PathVariable("product_id") UUID productId) {
        var resp = productHistoryService.getHistoryById(productId);
        return ResponseEntity.ok(resp.stream().map(productHistoryMapper::dtoToResponse).toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.CREATE_IP_PRODUCT)
    public ResponseEntity<MessageResponse<IpProductResponse>> createProduct(
            @RequestBody @Valid IpProductRequest productRequest
    ) {
        var productDTO = productMapper.requestToDto(productRequest);
        var resp = productService.createIpProduct(productDTO, false);
        productHistoryService.addHistory(IpProductHistoryActions.CREATE, null, resp);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new MessageResponse<>(
                        SUCCESS_TITLE,
                        simpleMessage("ip.product.created"),
                        productMapper.dtoToResponse(resp)
                )
        );
    }

    @PutMapping("/{idProduct}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_IP_PRODUCT)
    public ResponseEntity<MessageResponse<IpProductResponse>> updateProduct(
            @RequestBody @Valid IpProductRequest productRequest,
            @PathVariable UUID idProduct) {
        var oldProduct = productService.findIpProductById(idProduct);
        var productDTO = productMapper.requestToDto(productRequest);
        var resp = productService.updateIpProductById(idProduct, productDTO);
        productHistoryService.addHistory(IpProductHistoryActions.UPDATE, oldProduct, resp);
        return ResponseEntity.status(HttpStatus.OK).body(
                new MessageResponse<>(
                        SUCCESS_TITLE,
                        simpleMessage("ip.product.updated"),
                        productMapper.dtoToResponse(resp)
                )
        );
    }

    @PatchMapping("/open-lock/{idProduct}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.IP_PRODUCTS)
    public ResponseEntity<OpenLockIpProductResponse> openLockProduct(
            @PathVariable UUID idProduct,
            @RequestParam OpenAndLockType type
    ) {
        var product = productService.openAndLockIpProduct(idProduct, type);
        return ResponseEntity.status(HttpStatus.OK).body(
                new OpenLockIpProductResponse(
                        productMapper.dtoToResponse(product),
                        isOpenByUsername(product.getOpenBy(), type)
        ));
    }

    @PatchMapping("/close/{idProduct}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.IP_PRODUCTS)
    public ResponseEntity<MessageResponse<UUID>> closeProduct(
            @PathVariable UUID idProduct
    ) {
        productService.unlockIpProduct(idProduct);
        return ResponseEntity.status(HttpStatus.OK).body(
                new MessageResponse<>(
                        SUCCESS_TITLE,
                        simpleMessage("ip.product.closed"),
                        idProduct
                ));
    }

    @GetMapping("/load-open")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.IP_PRODUCTS)
    public ResponseEntity<List<ListIpProductResponse>> loadOpenProducts() {
        var list = productService.listAllOpenIpProducts(getUserAuthenticated());
        return ResponseEntity.ok(
                list.stream().map(productMapper::dtoToListResponse).toList()
        );
    }

    @PatchMapping("/close-list")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.IP_PRODUCTS)
    public ResponseEntity<MessageResponse<List<UUID>>> closeListIpProducts(
            @RequestBody List<UUID> listProductIds
    ) {
        listProductIds.forEach(item -> {
            if (item != null) {
                productService.unlockIpProduct(item);
            }
        });
        return ResponseEntity
                .ok(
                        new MessageResponse<>(
                                SUCCESS_TITLE,
                                simpleMessage("ip.product.all-closed"),
                                listProductIds
                        )
                );
    }

    @PatchMapping("/enable/{idProduct}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.ENABLE_IP_PRODUCT)
    public ResponseEntity<MessageResponse<ListIpProductResponse>> enabledProduct(
            @PathVariable UUID idProduct
    ) {
        var product = productService.enableIpProductById(idProduct);
        productHistoryService.addHistory(IpProductHistoryActions.ENABLE, null, product);
        return ResponseEntity.status(HttpStatus.OK).body(
                new MessageResponse<>(
                        SUCCESS_TITLE,
                        simpleMessage("ip.product.enabled"),
                        productMapper.dtoToListResponse(product)
                ));
    }

    @DeleteMapping("/{idProduct}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.DISABLE_IP_PRODUCT)
    public ResponseEntity<MessageResponse<ListIpProductResponse>> disableProduct(
            @PathVariable UUID idProduct
    ) {
        var product = productService.disableIpProductById(idProduct);
        productHistoryService.addHistory(IpProductHistoryActions.DISABLE, null, product);
        return ResponseEntity.status(HttpStatus.OK).body(
                new MessageResponse<>(
                        SUCCESS_TITLE,
                        simpleMessage("ip.product.disabled"),
                        productMapper.dtoToListResponse(product)
                ));
    }

    @PatchMapping("/replace")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.REPLACE_IP_PRODUCT)
    public ResponseEntity<MessageResponse<IpProductResponse>> replaceProduct(
            @RequestBody @Valid ReplaceIpProductRequest newProductId
    ) {
        var product = productService.replaceProduct(newProductId.getProductId(), newProductId.getNewProductId());
        var newProduct = productService.findIpProductById(newProductId.getNewProductId());
        productHistoryService.addHistory(IpProductHistoryActions.REPLACE, product, newProduct);
        return ResponseEntity.status(HttpStatus.OK).body(
                new MessageResponse<>(
                        SUCCESS_TITLE,
                        simpleMessage("ip.product.replaced"),
                        productMapper.dtoToResponse(product)
                ));
    }

    @PostMapping("/surplus/add/{idProduct}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_IP_PRODUCT)
    public ResponseEntity<MessageResponse<IpProductResponse>> addSurplusProduct(
            @PathVariable UUID idProduct,
            @RequestBody @Valid IpProductAddSurplusRequest request) {
        var oldProduct = productService.findIpProductById(idProduct);
        var surplusDto = productMapper.addRequestToDTO(request);
        var newProduct = productService.addSurplusIpProduct(idProduct, surplusDto);
        productHistoryService.addHistory(IpProductHistoryActions.ADD_SURPLUS, oldProduct, newProduct);
        return ResponseEntity.status(HttpStatus.OK).body(
                new MessageResponse<>(
                        SUCCESS_TITLE,
                        simpleMessage("ip.product.add-surplus"),
                        productMapper.dtoToResponse(newProduct)
                ));
    }

    @PostMapping("/surplus/out/{idProduct}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_IP_PRODUCT)
    public ResponseEntity<MessageResponse<IpProductResponse>> outSurplusProduct(
            @PathVariable UUID idProduct,
            @RequestBody @Valid IpProductOutSurplusRequest request) {
        var oldProduct = productService.findIpProductById(idProduct);
        var surplusDto = productMapper.outRequestToDTO(request);
        var newProduct = productService.outSurplusIpProduct(idProduct, surplusDto);
        productHistoryService.addHistory(IpProductHistoryActions.REMOVE_SURPLUS, oldProduct, newProduct);
        return ResponseEntity.status(HttpStatus.OK).body(
                new MessageResponse<>(
                        SUCCESS_TITLE,
                        simpleMessage("ip.product.out-surplus"),
                        productMapper.dtoToResponse(newProduct)
                ));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.IP_PRODUCTS)
    public ResponseEntity<Page<ListIpProductResponse>> listAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @ModelAttribute FilterListIpProducts filters
    ) {
        var resp = productService.listAllProducts(filters.getPageRequest(page, size), filters);
        var list = resp.getContent().stream()
                .map(productMapper::dtoToListResponse).toList();
        return ResponseEntity.ok(new PageImpl<>(list, resp.getPageable(),resp.getTotalElements()));
    }

    @GetMapping("/basic")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<BasicIpProductResponse>> listAllActiveProducts() {
        var resp = productService.listAllActiveProducts();
        return ResponseEntity.ok(resp.stream().map(productMapper::dtoToBasicResponse).toList());
    }

    @PatchMapping("/validate-import")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.IMPORT_IP_PRODUCT)
    public ResponseEntity<List<IpProductImportResponse>> validateImportProduct(
            @RequestBody @Valid List<IpProductsImportRequest> importProductsRequest
    ) {
        var data = importProductsRequest.stream().map(productMapper::importToDTO).toList();
        var resp = productService.validateImportProducts(data);
        return ResponseEntity.ok(resp.stream().map(productMapper::dtoToImportResponse).toList());
    }

    @PostMapping("/import-products")
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.IMPORT_IP_PRODUCT)
    public ResponseEntity<MessageResponse<List<IpProductResponse>>> importProducts(
            @RequestBody @Valid List<IpImportProductRequest> productSRequest
    ) {
        List<IpProductResponse> listResp = new ArrayList<>();
        productSRequest.forEach(prod -> {
            if (prod.isValidImport()) {

                if (!prod.isSaveBrand())
                    prod.setBrandId(null);

                if (!prod.isSaveCoo())
                    prod.setCooId(null);

                var productDTO = productMapper.requestToDto(prod);
                var resp = productService.createIpProduct(productDTO, true);
                productHistoryService.addHistory(IpProductHistoryActions.IMPORT_PRODUCT, null, resp);
                listResp.add(productMapper.dtoToResponse(resp));
            }
        });

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new MessageResponse<>(
                        SUCCESS_TITLE,
                        compositeMessage("ip.product.import", new String[]{listResp.size() + ""}),
                        listResp
                )
        );
    }


}
