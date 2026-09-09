package com.itradingsolutions.itex.api.partners.suppliers.models.mappers;

import com.itradingsolutions.itex.api.admin.user.models.mappers.UserMapper;
import com.itradingsolutions.itex.api.masters.brand.models.entities.BrandEntity;
import com.itradingsolutions.itex.api.masters.brand.models.entities.BrandSupplierEntity;
import com.itradingsolutions.itex.api.partners.suppliers.models.dto.SupplierDTO;
import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierEntity;
import com.itradingsolutions.itex.api.partners.suppliers.models.responses.BasicSupplierResponse;
import com.itradingsolutions.itex.api.partners.suppliers.models.responses.SupplierResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = UserMapper.class)
public interface SupplierMapper {
    SupplierDTO entityToDto(SupplierEntity entity);
    SupplierResponse entityToResponse(SupplierEntity entity);
    BasicSupplierResponse entityToBasicResponse(SupplierEntity entity);
    SupplierResponse dtoToResponse(SupplierDTO dto);
    BasicSupplierResponse dtoToBasicResponse(SupplierDTO dto);

    default List<String> map(List<BrandSupplierEntity> brands) {
        if (brands == null) return null;
        return brands.stream()
                .map(BrandSupplierEntity::getBrand)
                .filter(Objects::nonNull)
                .filter(BrandEntity::isActive)
                .map(BrandEntity::getName)
                .toList();
    }
}
