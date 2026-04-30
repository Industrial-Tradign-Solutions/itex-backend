package com.itradingsolutions.itex.api.ip.products.models.mappers;

import com.itradingsolutions.itex.api.ip.products.models.dto.IpProductHistoryDTO;
import com.itradingsolutions.itex.api.ip.products.models.entity.IpProductHistoryEntity;
import com.itradingsolutions.itex.api.ip.products.models.responses.IpProductHistoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IpProductHistoryMapper {
    IpProductHistoryDTO entityToDTO(IpProductHistoryEntity entity);
    IpProductHistoryResponse dtoToResponse(IpProductHistoryDTO dto);
}
