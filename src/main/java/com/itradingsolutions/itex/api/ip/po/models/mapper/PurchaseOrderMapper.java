package com.itradingsolutions.itex.api.ip.po.models.mapper;

import com.itradingsolutions.itex.api.ip.po.models.dto.PurchaseOrderDTO;
import com.itradingsolutions.itex.api.ip.po.models.entities.PurchaseOrderEntity;
import com.itradingsolutions.itex.api.ip.po.models.request.CreatePurchaseOrderRequest;
import com.itradingsolutions.itex.api.ip.po.models.response.PurchaseOrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {PurchaseOrderProductMapper.class, PurchaseOrderOtherChargeMapper.class,
                PurchaseOrderOtherChargesQuotationMapper.class, PurchaseOrderOtherChargesQuotationQrMapper.class})
public interface PurchaseOrderMapper {

    PurchaseOrderDTO entityToDTO(PurchaseOrderEntity entity);

    PurchaseOrderDTO requestToDTO(CreatePurchaseOrderRequest request);

    PurchaseOrderResponse dtoToResponse(PurchaseOrderDTO dto);
}
