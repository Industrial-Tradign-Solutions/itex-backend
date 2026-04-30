package com.itradingsolutions.itex.api.masters.brand.models.mappers;

import com.itradingsolutions.itex.api.masters.brand.models.dto.BrandDTO;
import com.itradingsolutions.itex.api.masters.brand.models.dto.BrandSupplierDTO;
import com.itradingsolutions.itex.api.masters.brand.models.entities.BrandEntity;
import com.itradingsolutions.itex.api.masters.brand.models.entities.BrandSupplierEntity;
import com.itradingsolutions.itex.api.masters.brand.models.requests.BrandRequest;
import com.itradingsolutions.itex.api.masters.brand.models.responses.BasicBrandResponse;
import com.itradingsolutions.itex.api.masters.brand.models.responses.BrandResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BrandMapper {

    BrandDTO entityToDto(BrandEntity entity);
    BrandResponse dtoToResponse(BrandDTO dto);
    BasicBrandResponse dtoToResponseBasic(BrandDTO dto);
    BasicBrandResponse requestToBasicResponse(BrandRequest request);
    BrandSupplierDTO entityToDto(BrandSupplierEntity entity);
}
