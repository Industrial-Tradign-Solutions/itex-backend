package com.itradingsolutions.itex.api.partners.suppliers.models.mappers;

import com.itradingsolutions.itex.api.partners.suppliers.models.dto.SupplierDTO;
import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierEntity;
import com.itradingsolutions.itex.api.partners.suppliers.models.responses.BasicSupplierResponse;
import com.itradingsolutions.itex.api.partners.suppliers.models.responses.SupplierResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SupplierMapper {
    SupplierDTO entityToDto(SupplierEntity entity);
    SupplierResponse dtoToResponse(SupplierDTO dto);
    BasicSupplierResponse dtoToBasicResponse(SupplierDTO dto);
}
