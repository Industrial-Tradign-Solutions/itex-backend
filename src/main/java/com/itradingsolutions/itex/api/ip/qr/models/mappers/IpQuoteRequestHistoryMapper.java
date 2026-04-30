package com.itradingsolutions.itex.api.ip.qr.models.mappers;

import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestHistoryDTO;
import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestHistoryEntity;
import com.itradingsolutions.itex.api.ip.qr.models.responses.IpQuoteRequestHistoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IpQuoteRequestHistoryMapper {
    IpQuoteRequestHistoryDTO entityToDTO(IpQuoteRequestHistoryEntity entity);
    IpQuoteRequestHistoryResponse dtoToResponse(IpQuoteRequestHistoryDTO dto);
}
