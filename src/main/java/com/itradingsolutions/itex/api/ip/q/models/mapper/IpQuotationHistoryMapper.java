package com.itradingsolutions.itex.api.ip.q.models.mapper;

import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationHistoryDTO;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationHistoryEntity;
import com.itradingsolutions.itex.api.ip.q.models.response.IpQuotationHistoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for IP Quotation history entities/DTOs/responses.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IpQuotationHistoryMapper {
    IpQuotationHistoryDTO entityToDTO(IpQuotationHistoryEntity entity);
    IpQuotationHistoryResponse dtoToResponse(IpQuotationHistoryDTO dto);
}
