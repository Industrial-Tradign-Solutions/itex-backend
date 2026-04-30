package com.itradingsolutions.itex.api.partners.suppliers.services;

import com.itradingsolutions.itex.api.partners.suppliers.models.dto.SupplierContactDTO;
import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierContactEntity;
import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierInfoDepEntity;
import com.itradingsolutions.itex.api.partners.suppliers.models.requests.SupplierInfoDepRequest;

import java.util.List;
import java.util.UUID;

public interface ISupplierContactService {

    List<SupplierContactDTO> saveListSupplierContacts(SupplierInfoDepEntity supplierInfoDepEntity, SupplierInfoDepRequest supplierInfoDepRequest, boolean isUpdate);
    SupplierContactEntity findById(UUID id, UUID supplierId);
}
