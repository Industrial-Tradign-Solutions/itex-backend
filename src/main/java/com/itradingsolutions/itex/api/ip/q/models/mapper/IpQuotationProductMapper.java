package com.itradingsolutions.itex.api.ip.q.models.mapper;

import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationProductDTO;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationProductEntity;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationsQuoteRequestEntity;
import com.itradingsolutions.itex.api.ip.q.models.response.IpQuotationProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IpQuotationProductMapper {

    @Mapping(target = "quotationsQuoteRequestId", source = "quotationsQuoteRequest.id")
    @Mapping(target = "quoteRequestProduct", source = "quoteRequestProduct")
    IpQuotationProductDTO entityToDto(IpQuotationProductEntity entity);

    @Mapping(target = "quotationsQuoteRequestId", source = "quotationsQuoteRequestId")
    @Mapping(target = "sellingUnitPrice", expression = "java(dto.getSellingUnitPrice())")
    @Mapping(target = "extendedPrice", expression = "java(dto.getExtendedPrice())")
    @Mapping(target = "grossWeightLbs", expression = "java(dto.getGrossWeightLbs())")
    IpQuotationProductResponse dtoToResponse(IpQuotationProductDTO dto);

    default UUID mapJunctionId(IpQuotationsQuoteRequestEntity entity) {
        return entity != null ? entity.getId() : null;
    }
}
