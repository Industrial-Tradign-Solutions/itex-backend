package com.itradingsolutions.itex.api.ip.po.models.mapper;

import com.itradingsolutions.itex.api.ip.po.models.dto.IpPurchaseOrderOtherChargesQuotationQrDTO;
import com.itradingsolutions.itex.api.ip.po.models.entities.IpPurchaseOrderOtherChargesQuotationQrEntity;
import com.itradingsolutions.itex.api.ip.po.models.response.IpPurchaseOrderOtherChargesQuotationQrResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IpPurchaseOrderOtherChargesQuotationQrMapper {

    IpPurchaseOrderOtherChargesQuotationQrDTO entityToDto(IpPurchaseOrderOtherChargesQuotationQrEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "purchaseOrder", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    IpPurchaseOrderOtherChargesQuotationQrEntity clone(IpPurchaseOrderOtherChargesQuotationQrEntity entity);

    IpPurchaseOrderOtherChargesQuotationQrResponse dtoToResponse(IpPurchaseOrderOtherChargesQuotationQrDTO dto);
}
