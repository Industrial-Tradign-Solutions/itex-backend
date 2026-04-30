package com.itradingsolutions.itex.api.partners.clients.models.mappers;

import com.itradingsolutions.itex.api.masters.brand.models.dto.BrandSupplierDTO;
import com.itradingsolutions.itex.api.masters.brand.models.entities.BrandSupplierEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SupplierBrandMapper {

    BrandSupplierDTO entityToDTO(BrandSupplierEntity entity);
}
