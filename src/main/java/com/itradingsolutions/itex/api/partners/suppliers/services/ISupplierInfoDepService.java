package com.itradingsolutions.itex.api.partners.suppliers.services;

import com.itradingsolutions.itex.api.partners.suppliers.models.dto.SupplierInfoDepDTO;
import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierEntity;
import com.itradingsolutions.itex.api.partners.suppliers.models.requests.SupplierRequest;

import java.util.List;

public interface ISupplierInfoDepService {
    List<SupplierInfoDepDTO> saveListSupplierInfoDep(SupplierRequest request, SupplierEntity supplierEntity, boolean isUpdate);
}
