package com.itradingsolutions.itex.api.partners.suppliers.controller;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleOption;
import com.itradingsolutions.itex.api.common.controller.CommonController;
import com.itradingsolutions.itex.api.common.models.enums.OpenAndLockType;
import com.itradingsolutions.itex.api.common.util.models.enums.HistoryActions;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import com.itradingsolutions.itex.api.partners.suppliers.models.dto.SupplierDTO;
import com.itradingsolutions.itex.api.partners.suppliers.models.enums.SupplierStatus;
import com.itradingsolutions.itex.api.partners.suppliers.models.filter.FilterListSuppliers;
import com.itradingsolutions.itex.api.partners.suppliers.models.mappers.SupplierMapper;
import com.itradingsolutions.itex.api.partners.suppliers.models.requests.SupplierRequest;
import com.itradingsolutions.itex.api.partners.suppliers.models.responses.BasicSupplierResponse;
import com.itradingsolutions.itex.api.partners.suppliers.models.responses.ListSupplierResponse;
import com.itradingsolutions.itex.api.partners.suppliers.models.responses.SupplierResponse;
import com.itradingsolutions.itex.api.partners.suppliers.services.ISupplierService;
import com.itradingsolutions.itex.config.security.auth.AccessToAction;
import com.itradingsolutions.itex.config.security.auth.AccessToModule;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/partners/suppliers")
@Validated
@RequiredArgsConstructor
public class SupplierController extends CommonController {

    private final ISupplierService supplierService;
    private final SupplierMapper supplierMapper;

    @GetMapping("/dashboard")
    @ResponseStatus(HttpStatus.OK)
    @Deprecated
    public ResponseEntity<Long> getDashboardSuppliers() {
        return ResponseEntity.ok(supplierService.dashboardSuppliers());
    }

    @GetMapping("/load-open-suppliers")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.SUPPLIERS)
    public ResponseEntity<List<ListSupplierResponse>> loadOpenSuppliers() {
        var list = supplierService.listAllOpenSupplier(getUserAuthenticated());
        return ResponseEntity.ok(
                list.stream().map(supplier ->
                        new ListSupplierResponse(
                                supplier.getId(),
                                supplier.getName(),
                                null,
                                null,
                                null,
                                null
                        )
                ).toList()
        );
    }

    @PutMapping("/close-list-suppliers")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.SUPPLIERS)
    public ResponseEntity<MessageResponse<List<UUID>>> closeListSuppliers(
            @RequestBody List<UUID> listSuppliersIds
    ) {
        listSuppliersIds.forEach(supplierId -> {
            if(supplierId != null) {
                supplierService.unlockSupplier(supplierId);
                historyService.saveHistory(HistoryActions.UNLOCK, ModuleOption.SUPPLIERS, supplierId, null, null);
            }
        });
        return ResponseEntity
                .ok(
                        new MessageResponse<>(
                                SUCCESS_TITLE,
                                simpleMessage("supplier.all-closed"),
                                listSuppliersIds
                        )
                );
    }

    @PutMapping("/close-supplier/{supplierId}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.SUPPLIERS)
    public ResponseEntity<MessageResponse<UUID>> closeSupplier(
            @PathVariable UUID supplierId) {
        supplierService.unlockSupplier(supplierId);
        historyService.saveHistory(HistoryActions.UNLOCK, ModuleOption.SUPPLIERS, supplierId, null, null);
        return ResponseEntity
                .ok(
                        new MessageResponse<>(
                                SUCCESS_TITLE,
                                simpleMessage("supplier.closed"),
                                supplierId
                        )
                );
    }

    @GetMapping("/open-and-lock/{supplierId}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.SUPPLIERS)
    public ResponseEntity<Map<String, Object>> openAndLockSuppliers(@PathVariable UUID supplierId, @RequestParam OpenAndLockType type) {
        Map<String, Object> resp = new HashMap<>();
        var supplier = supplierService.openAndLockSupplier(supplierId, type);

        resp.put("data", supplierMapper.dtoToResponse(supplier));
        var isValidOpen = isOpenByUsername(supplier.getOpenBy(), type);
        resp.put("isValidOpen", Optional.of(isValidOpen));
        if (isValidOpen)
            historyService.saveHistory(HistoryActions.LOCK, ModuleOption.SUPPLIERS, supplierId, null, null);
        return ResponseEntity.ok(resp);
    }

    private SupplierDTO getSupplier(UUID supplierId) {
        if (supplierId == null) return null;
        return supplierMapper.entityToDto(supplierService.findSupplierById(supplierId, false));
    }


    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @AccessToAction(action = ModuleAction.CREATE_SUPPLIER)
    public ResponseEntity<MessageResponse<SupplierResponse>> create(
            @RequestBody @Valid SupplierRequest supplierRequest
    ) {
        var resp = supplierService.createSupplier(supplierRequest);
        historyService.saveHistory(HistoryActions.CREATE, ModuleOption.SUPPLIERS, resp.getId(), null, resp);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        new MessageResponse<>(
                                SUCCESS_TITLE,
                                simpleMessage("supplier.created"),
                                supplierMapper.dtoToResponse(resp)
                        )
                );
    }

    @PutMapping("/update/{supplierId}")
    @ResponseStatus(HttpStatus.OK)
    @AccessToAction(action = ModuleAction.UPDATE_SUPPLIER)
    public ResponseEntity<MessageResponse<SupplierResponse>> update(
            @PathVariable UUID supplierId,
            @RequestBody @Valid SupplierRequest supplierRequest
    ) {
        var oldSupplier = getSupplier(supplierId);
        var resp = supplierService.updateSupplier(supplierRequest, supplierId);
        historyService.saveHistory(HistoryActions.UPDATE, ModuleOption.SUPPLIERS, resp.getId(), oldSupplier, resp);
        saveHistoryStatus(resp, oldSupplier);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new MessageResponse<>(
                                SUCCESS_TITLE,
                                simpleMessage("supplier.updated"),
                                supplierMapper.dtoToResponse(resp)
                        )
                );
    }

    @GetMapping("/list-active")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<BasicSupplierResponse>> listAllActive() {
        var resp = supplierService.listAllActive();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(resp.stream().map(supplierMapper::dtoToBasicResponse).toList());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @AccessToModule(option = ModuleOption.SUPPLIERS)
    public ResponseEntity<Page<ListSupplierResponse>> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @ModelAttribute FilterListSuppliers filters
    ) {
        var resp = supplierService.listAllSupplier(filters.getPageRequest(page, size), filters);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        new PageImpl<>(resp.getContent().stream().map(supplier ->
                                new ListSupplierResponse(
                                        supplier.getId(),
                                        supplier.getName(),
                                        supplier.getTaxId(),
                                        supplier.getCity() != null ? supplier.getCity().getFullName() : null,
                                        supplier.getAddress(),
                                        supplier.getStatus()
                                )
                        ).toList(),resp.getPageable(),resp.getTotalElements())
                );
    }

    private void saveHistoryStatus(SupplierDTO newSupplier, SupplierDTO oldSupplier) {
        if (!oldSupplier.getStatus().equals(newSupplier.getStatus())) {
            var action = HistoryActions.CHANGE_STATUS;
            if (newSupplier.getStatus().equals(SupplierStatus.ACTIVE)) {
                if (oldSupplier.getStatus().equals(SupplierStatus.INACTIVE)) {
                    action = HistoryActions.ENABLE;
                }
            } else if (newSupplier.getStatus().equals(SupplierStatus.INACTIVE)) {
                action = HistoryActions.DISABLE;
            }
            historyService.saveHistory(action, ModuleOption.SUPPLIERS, newSupplier.getId(), null, null);
        }
    }
}
