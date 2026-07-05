package com.itradingsolutions.itex.api.ip.q.models.mapper;

import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationOtherChargesQuoteRequestDTO;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationOtherChargesQuoteRequestEntity;
import com.itradingsolutions.itex.api.ip.q.models.response.IpQuotationOtherChargesQuoteRequestResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IpQuotationOtherChargesQuoteRequestMapper {

    @Mapping(target = "quotationsQuoteRequestId", source = "entity.quotationsQuoteRequest.id")
    @Mapping(target = "qrOtherCharge", source = "entity.qrOtherCharge")
    IpQuotationOtherChargesQuoteRequestDTO entityToDto(IpQuotationOtherChargesQuoteRequestEntity entity);

    IpQuotationOtherChargesQuoteRequestResponse dtoToResponse(IpQuotationOtherChargesQuoteRequestDTO dto);
}
