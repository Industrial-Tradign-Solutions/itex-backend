package com.itradingsolutions.itex.api.ip.po.models.mapper;

import com.itradingsolutions.itex.api.ip.po.models.dto.PurchaseOrderOtherChargesQuotationDTO;
import com.itradingsolutions.itex.api.ip.po.models.entities.PurchaseOrderOtherChargesQuotationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PurchaseOrderOtherChargesQuotationMapper {

    PurchaseOrderOtherChargesQuotationDTO entityToDto(PurchaseOrderOtherChargesQuotationEntity entity);
}
