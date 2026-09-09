package com.itradingsolutions.itex.api.partners.suppliers.services;

import com.itradingsolutions.itex.api.common.models.enums.OpenAndLockType;
import com.itradingsolutions.itex.api.partners.suppliers.models.dto.SupplierDTO;
import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierEntity;
import com.itradingsolutions.itex.api.partners.suppliers.models.filter.FilterListSuppliers;
import com.itradingsolutions.itex.api.partners.suppliers.models.requests.SupplierRequest;
import com.itradingsolutions.itex.api.partners.suppliers.models.responses.BasicSupplierResponse;
import com.itradingsolutions.itex.api.partners.suppliers.models.responses.ListSupplierResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ISupplierService {

    SupplierDTO createSupplier(SupplierRequest request);
    SupplierDTO updateSupplier(SupplierRequest request, UUID supplierId);
    SupplierDTO openAndLockSupplier(UUID supplierId, OpenAndLockType type);
    void unlockSupplier(UUID supplierId);
    Page<ListSupplierResponse> listAllSupplier(Pageable pageable, FilterListSuppliers filters);
    List<SupplierDTO> listAllOpenSupplier(String username);
    SupplierEntity findSupplierById(UUID supplierId, boolean validateActive);
    List<BasicSupplierResponse> listAllActive();

    @Deprecated(since = "Funcion para eliminar")
    long dashboardSuppliers();
}
