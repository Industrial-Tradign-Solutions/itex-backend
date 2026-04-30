package com.itradingsolutions.itex.api.partners.suppliers.models.mappers;

import com.itradingsolutions.itex.api.partners.suppliers.models.dto.SupplierInfoDepDTO;
import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierInfoDepEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SupplierInfoDepMapper {

    SupplierInfoDepDTO entityToDTO(SupplierInfoDepEntity entity);
}
