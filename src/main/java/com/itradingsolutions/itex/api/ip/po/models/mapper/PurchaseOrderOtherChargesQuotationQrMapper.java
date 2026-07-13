package com.itradingsolutions.itex.api.ip.po.models.mapper;

import com.itradingsolutions.itex.api.ip.po.models.dto.PurchaseOrderOtherChargesQuotationQrDTO;
import com.itradingsolutions.itex.api.ip.po.models.entities.PurchaseOrderOtherChargesQuotationQrEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PurchaseOrderOtherChargesQuotationQrMapper {

    PurchaseOrderOtherChargesQuotationQrDTO entityToDto(PurchaseOrderOtherChargesQuotationQrEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "purchaseOrder", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    PurchaseOrderOtherChargesQuotationQrEntity clone(PurchaseOrderOtherChargesQuotationQrEntity entity);
}
