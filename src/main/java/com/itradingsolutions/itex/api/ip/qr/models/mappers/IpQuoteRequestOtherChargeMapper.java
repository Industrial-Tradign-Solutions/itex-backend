package com.itradingsolutions.itex.api.ip.qr.models.mappers;

import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestOtherChargesDTO;
import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestOtherChargesEntity;
import com.itradingsolutions.itex.api.ip.qr.models.requests.IpQuoteRequestOtherChargeRequest;
import com.itradingsolutions.itex.api.ip.qr.models.responses.IpQuoteRequestOtherChargeResponse;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IpQuoteRequestOtherChargeMapper {

    IpQuoteRequestOtherChargesDTO requestToDTO(IpQuoteRequestOtherChargeRequest request);
    IpQuoteRequestOtherChargesDTO entityToDto(IpQuoteRequestOtherChargesEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ipQuoteRequest", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    IpQuoteRequestOtherChargesEntity clone(IpQuoteRequestOtherChargesEntity entity);
    IpQuoteRequestOtherChargeResponse dtoToResponse(IpQuoteRequestOtherChargesDTO dto);
}
