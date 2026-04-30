package com.itradingsolutions.itex.api.ip.qr.models.mappers;

import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestDTO;
import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestEntity;
import com.itradingsolutions.itex.api.ip.qr.models.requests.IpQuoteRequestRequest;
import com.itradingsolutions.itex.api.ip.qr.models.responses.IpQuoteRequestResponse;
import com.itradingsolutions.itex.api.ip.qr.models.responses.ListIpQuoteRequestResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IpQuoteRequestMapper {
    IpQuoteRequestDTO entityToDTO(IpQuoteRequestEntity entity);
    IpQuoteRequestResponse dtoToResponse(IpQuoteRequestDTO dto);
    IpQuoteRequestDTO requestToDTO(IpQuoteRequestRequest request);
    ListIpQuoteRequestResponse dtoToListResponse(IpQuoteRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "number", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "salesRep", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "supplierContact", ignore = true)
    @Mapping(target = "supplierQrNumber", ignore = true)
    @Mapping(target = "paymentTerms", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "clonedQrs", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    IpQuoteRequestEntity clone(IpQuoteRequestEntity entity);
}
