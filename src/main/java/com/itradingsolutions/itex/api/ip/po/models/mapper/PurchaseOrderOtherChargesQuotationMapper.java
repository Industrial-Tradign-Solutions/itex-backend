package com.itradingsolutions.itex.api.ip.po.models.mapper;

import com.itradingsolutions.itex.api.ip.po.models.dto.PurchaseOrderOtherChargesQuotationDTO;
import com.itradingsolutions.itex.api.ip.po.models.entities.PurchaseOrderOtherChargesQuotationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PurchaseOrderOtherChargesQuotationMapper {

    PurchaseOrderOtherChargesQuotationDTO entityToDto(PurchaseOrderOtherChargesQuotationEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "purchaseOrder", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    PurchaseOrderOtherChargesQuotationEntity clone(PurchaseOrderOtherChargesQuotationEntity entity);
}
