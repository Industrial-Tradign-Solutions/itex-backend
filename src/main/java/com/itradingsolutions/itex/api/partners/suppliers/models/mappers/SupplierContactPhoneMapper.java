package com.itradingsolutions.itex.api.partners.suppliers.models.mappers;

import com.itradingsolutions.itex.api.partners.suppliers.models.dto.SupplierContactPhoneDTO;
import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierContactPhoneEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SupplierContactPhoneMapper {
    SupplierContactPhoneDTO entityToDTO(SupplierContactPhoneEntity entity);
}
