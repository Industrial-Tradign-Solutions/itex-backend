package com.itradingsolutions.itex.api.ip.po.models.mapper;

import com.itradingsolutions.itex.api.ip.po.models.dto.IpPurchaseOrderOtherChargesQuotationDTO;
import com.itradingsolutions.itex.api.ip.po.models.entities.IpPurchaseOrderOtherChargesQuotationEntity;
import com.itradingsolutions.itex.api.ip.po.models.response.IpPurchaseOrderOtherChargesQuotationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IpPurchaseOrderOtherChargesQuotationMapper {

    IpPurchaseOrderOtherChargesQuotationDTO entityToDto(IpPurchaseOrderOtherChargesQuotationEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "purchaseOrder", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    IpPurchaseOrderOtherChargesQuotationEntity clone(IpPurchaseOrderOtherChargesQuotationEntity entity);

    IpPurchaseOrderOtherChargesQuotationResponse dtoToResponse(IpPurchaseOrderOtherChargesQuotationDTO dto);
}
