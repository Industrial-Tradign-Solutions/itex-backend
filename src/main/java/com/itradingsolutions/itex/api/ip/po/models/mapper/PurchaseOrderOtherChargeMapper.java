package com.itradingsolutions.itex.api.ip.po.models.mapper;

import com.itradingsolutions.itex.api.ip.po.models.dto.PurchaseOrderOtherChargeDTO;
import com.itradingsolutions.itex.api.ip.po.models.entities.PurchaseOrderOtherChargeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PurchaseOrderOtherChargeMapper {

    PurchaseOrderOtherChargeDTO entityToDto(PurchaseOrderOtherChargeEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "purchaseOrder", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    PurchaseOrderOtherChargeEntity clone(PurchaseOrderOtherChargeEntity entity);
}
