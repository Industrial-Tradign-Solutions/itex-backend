package com.itradingsolutions.itex.api.partners.suppliers.services;

import com.itradingsolutions.itex.api.partners.suppliers.models.dto.SupplierContactPhoneDTO;
import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierContactEntity;
import com.itradingsolutions.itex.api.partners.suppliers.models.requests.SupplierContactRequest;

import java.util.List;

public interface ISupplierContactPhoneService {

    List<SupplierContactPhoneDTO> saveListSupplierContactPhones(SupplierContactEntity supplierContactEntity, SupplierContactRequest supplierContactRequest, boolean isUpdate);
}
