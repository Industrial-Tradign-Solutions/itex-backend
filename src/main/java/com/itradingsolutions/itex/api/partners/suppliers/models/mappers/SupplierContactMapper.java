package com.itradingsolutions.itex.api.partners.suppliers.models.mappers;

import com.itradingsolutions.itex.api.partners.suppliers.models.dto.SupplierContactDTO;
import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierContactEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SupplierContactMapper {
    SupplierContactDTO entityToDTO(SupplierContactEntity entity);
}
