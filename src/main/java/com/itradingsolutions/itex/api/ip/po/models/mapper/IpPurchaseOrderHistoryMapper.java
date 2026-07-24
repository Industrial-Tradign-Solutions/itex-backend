package com.itradingsolutions.itex.api.ip.po.models.mapper;

import com.itradingsolutions.itex.api.ip.po.models.dto.IpPurchaseOrderHistoryDTO;
import com.itradingsolutions.itex.api.ip.po.models.entities.IpPurchaseOrderHistoryEntity;
import com.itradingsolutions.itex.api.ip.po.models.response.IpPurchaseOrderHistoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IpPurchaseOrderHistoryMapper {

    IpPurchaseOrderHistoryDTO entityToDTO(IpPurchaseOrderHistoryEntity entity);

    IpPurchaseOrderHistoryResponse dtoToResponse(IpPurchaseOrderHistoryDTO dto);
}
