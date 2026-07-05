package com.itradingsolutions.itex.api.ip.q.models.mapper;

import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationProductDTO;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationProductEntity;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationsQuoteRequestEntity;
import com.itradingsolutions.itex.api.ip.q.models.response.IpQuotationProductResponse;
import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestEntity;
import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Optional;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IpQuotationProductMapper {

    @Mapping(target = "quotationsQuoteRequestId", source = "quotationsQuoteRequest.id")
    @Mapping(target = "quoteRequestProduct", source = "quoteRequestProduct")
    @Mapping(target = "qrNumber", expression = "java(mapQrNumber(entity))")
    @Mapping(target = "supplierName", expression = "java(mapSupplierName(entity))")
    IpQuotationProductDTO entityToDto(IpQuotationProductEntity entity);

    IpQuotationProductResponse dtoToResponse(IpQuotationProductDTO dto);

    default String mapQrNumber(IpQuotationProductEntity entity) {
        return Optional.ofNullable(entity)
                .map(IpQuotationProductEntity::getQuotationsQuoteRequest)
                .map(IpQuotationsQuoteRequestEntity::getQuoteRequest)
                .map(IpQuoteRequestEntity::getNumber)
                .orElse(null);
    }

    default String mapSupplierName(IpQuotationProductEntity entity) {
        return Optional.ofNullable(entity)
                .map(IpQuotationProductEntity::getQuotationsQuoteRequest)
                .map(IpQuotationsQuoteRequestEntity::getQuoteRequest)
                .map(IpQuoteRequestEntity::getSupplier)
                .map(SupplierEntity::getName)
                .orElse(null);
    }
}
