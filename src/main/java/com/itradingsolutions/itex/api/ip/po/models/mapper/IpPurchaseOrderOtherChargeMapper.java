package com.itradingsolutions.itex.api.ip.po.models.mapper;

import com.itradingsolutions.itex.api.ip.po.models.dto.IpPurchaseOrderOtherChargeDTO;
import com.itradingsolutions.itex.api.ip.po.models.entities.IpPurchaseOrderOtherChargeEntity;
import com.itradingsolutions.itex.api.ip.po.models.request.IpPurchaseOrderOtherChargeRequest;
import com.itradingsolutions.itex.api.ip.po.models.response.IpPurchaseOrderOtherChargeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IpPurchaseOrderOtherChargeMapper {

    IpPurchaseOrderOtherChargeDTO requestToDTO(IpPurchaseOrderOtherChargeRequest request);

    IpPurchaseOrderOtherChargeDTO entityToDto(IpPurchaseOrderOtherChargeEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "purchaseOrder", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    IpPurchaseOrderOtherChargeEntity clone(IpPurchaseOrderOtherChargeEntity entity);

    IpPurchaseOrderOtherChargeResponse dtoToResponse(IpPurchaseOrderOtherChargeDTO dto);
}
