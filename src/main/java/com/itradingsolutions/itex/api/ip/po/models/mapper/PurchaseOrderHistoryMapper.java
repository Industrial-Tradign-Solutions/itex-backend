package com.itradingsolutions.itex.api.ip.po.models.mapper;

import com.itradingsolutions.itex.api.ip.po.models.dto.PurchaseOrderHistoryDTO;
import com.itradingsolutions.itex.api.ip.po.models.entities.PurchaseOrderHistoryEntity;
import com.itradingsolutions.itex.api.ip.po.models.response.PurchaseOrderHistoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PurchaseOrderHistoryMapper {

    PurchaseOrderHistoryDTO entityToDTO(PurchaseOrderHistoryEntity entity);

    PurchaseOrderHistoryResponse dtoToResponse(PurchaseOrderHistoryDTO dto);
}
