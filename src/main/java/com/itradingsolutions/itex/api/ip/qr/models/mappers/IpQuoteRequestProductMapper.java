package com.itradingsolutions.itex.api.ip.qr.models.mappers;

import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestProductDTO;
import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestProductEntity;
import com.itradingsolutions.itex.api.ip.qr.models.requests.IpQuoteRequestProductRequest;
import com.itradingsolutions.itex.api.ip.qr.models.responses.IpQuoteRequestProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IpQuoteRequestProductMapper {
    IpQuoteRequestProductDTO requestToProductDTO(IpQuoteRequestProductRequest request);
    IpQuoteRequestProductDTO entityToDto(IpQuoteRequestProductEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ipQuoteRequest", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    IpQuoteRequestProductEntity clone(IpQuoteRequestProductEntity entity);

    IpQuoteRequestProductResponse dtoToResponse(IpQuoteRequestProductDTO dto);
}
