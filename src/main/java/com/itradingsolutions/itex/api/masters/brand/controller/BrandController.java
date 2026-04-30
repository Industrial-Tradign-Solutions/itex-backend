package com.itradingsolutions.itex.api.masters.brand.controller;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleOption;
import com.itradingsolutions.itex.api.common.controller.CommonController;
import com.itradingsolutions.itex.api.common.util.models.enums.HistoryActions;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import com.itradingsolutions.itex.api.masters.brand.models.dto.BrandDTO;
import com.itradingsolutions.itex.api.masters.brand.models.filters.BrandFilter;
import com.itradingsolutions.itex.api.masters.brand.models.mappers.BrandMapper;
import com.itradingsolutions.itex.api.masters.brand.models.requests.AssignBrandSupplierListRequest;
import com.itradingsolutions.itex.api.masters.brand.models.requests.AssignBrandSupplierRequest;
import com.itradingsolutions.itex.api.masters.brand.models.requests.BrandRequest;
import com.itradingsolutions.itex.api.masters.brand.models.responses.BasicBrandResponse;
import com.itradingsolutions.itex.api.masters.brand.models.responses.BrandResponse;
import com.itradingsolutions.itex.api.masters.brand.models.responses.ListsBrandResponse;
import com.itradingsolutions.itex.api.masters.brand.services.IBrandService;
import com.itradingsolutions.itex.api.partners.suppliers.models.mappers.SupplierMapper;
import com.itradingsolutions.itex.api.partners.suppliers.models.responses.SupplierResponse;
import com.itradingsolutions.itex.config.security.auth.AccessToAction;
import com.itradingsolutions.itex.config.security.auth.AccessToModule;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/master/brands")
@Validated
@RequiredArgsConstructor
public class BrandController extends CommonController {

    private final IBrandService brandService;
    private final BrandMapper brandMapper;
    private final SupplierMapper supplierMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.BRANDS)
    public ResponseEntity<PageImpl<BrandResponse>> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @ModelAttribute BrandFilter filters
    ) {
        var resp = brandService.listAll(filters.getPageRequest(page, size), filters);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new PageImpl<>(resp.getContent().stream().map(brandMapper::dtoToResponse
                        ).toList(),resp.getPageable(),resp.getTotalElements())
                );
    }

    @GetMapping("/basic")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ListsBrandResponse> listAllBasicBrands() {
        var resp = brandService.listAllBrands();
        return ResponseEntity.ok(new ListsBrandResponse(
                resp.stream().filter(BrandDTO::isActive).map(brandMapper::dtoToResponseBasic).toList(),
                resp.stream().filter(b -> !b.isActive()).map(brandMapper::dtoToResponseBasic).toList()
        ));
    }

    @GetMapping("/list-suppliers/{brandId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<SupplierResponse>> listSuppliersByBrandId(
            @PathVariable UUID brandId) {
        var list = brandService.listSuppliersByBrandId(brandId);
        return ResponseEntity.ok(list.stream().map(item -> supplierMapper.dtoToResponse(item.getSupplier())).toList());
    }

    @PostMapping("/validate-import")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.IMPORT_EXCEL_BRAND)
    public ResponseEntity<List<BasicBrandResponse>> validateImportExcelBrand(@RequestParam MultipartFile file) {
        List<String> listBrandNames = brandService.getListBrands(file);

        List<BasicBrandResponse> resp = new ArrayList<>();
        for (String name: listBrandNames) {
            var brand = brandService.findByName(name).orElseGet(() -> {
                var dto = new BrandDTO();
                dto.setName(name);
                return dto;
            });
            resp.add(brandMapper.dtoToResponseBasic(brand));
        }
        return ResponseEntity.ok(resp);
    }
    @PostMapping("/import")
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.IMPORT_EXCEL_BRAND)
    public ResponseEntity<MessageResponse<List<BasicBrandResponse>>> importExcelBrand(@RequestBody @Valid List<BrandRequest> requests) {
        List<BrandDTO> resp = brandService.createList(requests.stream().filter(b -> b.getId() == null).toList());
        historyService.importList(ModuleOption.BRANDS, resp);
        List<BasicBrandResponse> respList = new ArrayList<>();
        respList.addAll(requests.stream().filter(b -> b.getId() != null).map(brandMapper::requestToBasicResponse).toList());
        respList.addAll(resp.stream().map(brandMapper::dtoToResponseBasic).toList());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new MessageResponse<>(
                                SUCCESS_TITLE,
                                simpleMessage("brand.created"),
                                respList
                        )
                );
    }

    @PostMapping("/assign-brand-supplier-list")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.IMPORT_EXCEL_BRAND)
    public ResponseEntity<MessageResponse<Void>> assignListBrandsSupplier(
            @RequestBody @Valid AssignBrandSupplierListRequest requests) {
        brandService.assignBrandSupplierList(requests);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new MessageResponse<>(
                                SUCCESS_TITLE,
                                simpleMessage("brand.created"),
                                null
                        )
                );
    }

    @DeleteMapping("/disable/{brandId}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.DISABLE_BRAND)
    public ResponseEntity<MessageResponse<UUID>> disable(
            @PathVariable UUID brandId
    ) {
        brandService.disable(brandId);
        historyService.saveHistory(HistoryActions.DISABLE, ModuleOption.BRANDS, brandId, null, null);
        return ResponseEntity
                .ok(
                        new MessageResponse<>(
                                SUCCESS_TITLE,
                                simpleMessage("brand.disabled"),
                                brandId
                        )
                );
    }

    @DeleteMapping("/unassign-supplier/{brandId}/{supplierId}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.DELETE_SUPPLIER_BRAND)
    public ResponseEntity<MessageResponse<UUID>> unAssignSupplier(
            @PathVariable UUID brandId,
            @PathVariable UUID supplierId
    ) {
        brandService.unAssignSupplier(supplierId, brandId);
        Map<String, Object> data = new HashMap<>();
        data.put("supplierId", supplierId);
        data.put("brandId", brandId);
        data.put("action", "Unassign Supplier by Brand ID");
        historyService.saveHistory(HistoryActions.UPDATE, ModuleOption.BRANDS, brandId, null, data);
        return ResponseEntity
                .ok(
                        new MessageResponse<>(
                                SUCCESS_TITLE,
                                simpleMessage("brand.unassigned"),
                                brandId
                        )
                );
    }

    @PatchMapping("/enable/{brandId}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.ENABLE_BRAND)
    public ResponseEntity<MessageResponse<UUID>> enable(
            @PathVariable UUID brandId
    ) {
        brandService.enable(brandId);
        historyService.saveHistory(HistoryActions.ENABLE, ModuleOption.BRANDS, brandId, null, null);
        return ResponseEntity
                .ok(
                        new MessageResponse<>(
                                SUCCESS_TITLE,
                                simpleMessage("brand.enabled"),
                                brandId
                        )
                );
    }

    @PostMapping("/assign-brand-supplier")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MessageResponse<Void>> assignBrandSupplier(
            @RequestBody @Valid AssignBrandSupplierRequest request
    ) {
        brandService.assignBrandSupplier(request.getBrandId(), request.getSupplierId());
        return ResponseEntity.ok(new MessageResponse<>(
                SUCCESS_TITLE,
                simpleMessage("brand.assigned"),
                null
        ));
    }

    @PostMapping("/validate-brand")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<BasicBrandResponse> validateBrand(
            @RequestBody String brandName
    ) {
        var brandConvert = brandService.convertBrandName(brandName);
        var brandDto = brandService.findByName(brandConvert).orElse(new BrandDTO());
        brandDto.setName(brandName);
        return ResponseEntity.ok(brandMapper.dtoToResponseBasic(brandDto));
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.CREATE_BRAND)
    public ResponseEntity<MessageResponse<BrandResponse>> create(
            @RequestBody @Valid BrandRequest brandRequest
    ) {
        var dto = brandService.create(brandRequest);
        historyService.saveHistory(HistoryActions.CREATE, ModuleOption.BRANDS, dto.getId(), null, dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new MessageResponse<>(
                                SUCCESS_TITLE,
                                simpleMessage("brand.created"),
                                brandMapper.dtoToResponse(dto)
                        )
                );
    }
}
