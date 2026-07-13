package com.itradingsolutions.itex.api.ip.po.models.mapper;

import com.itradingsolutions.itex.api.ip.po.models.dto.PurchaseOrderDTO;
import com.itradingsolutions.itex.api.ip.po.models.entities.PurchaseOrderEntity;
import com.itradingsolutions.itex.api.ip.po.models.request.CreatePurchaseOrderRequest;
import com.itradingsolutions.itex.api.ip.po.models.response.ListPurchaseOrderResponse;
import com.itradingsolutions.itex.api.ip.po.models.response.PurchaseOrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {PurchaseOrderProductMapper.class, PurchaseOrderOtherChargeMapper.class,
                PurchaseOrderOtherChargesQuotationMapper.class, PurchaseOrderOtherChargesQuotationQrMapper.class})
public interface PurchaseOrderMapper {

    PurchaseOrderDTO entityToDTO(PurchaseOrderEntity entity);

    PurchaseOrderResponse dtoToResponse(PurchaseOrderDTO dto);

    ListPurchaseOrderResponse dtoToListResponse(PurchaseOrderDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "number", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "salesRep", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "supplierContact", ignore = true)
    @Mapping(target = "supplierPoNumber", ignore = true)
    @Mapping(target = "paymentTerms", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "otherCharges", ignore = true)
    @Mapping(target = "importedQuotationCharges", ignore = true)
    @Mapping(target = "importedQuoteRequestCharges", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "pdfUrl", ignore = true)
    @Mapping(target = "openBy", ignore = true)
    @Mapping(target = "openAt", ignore = true)
    @Mapping(target = "sentAt", ignore = true)
    @Mapping(target = "answeredAt", ignore = true)
    @Mapping(target = "completeAt", ignore = true)
    @Mapping(target = "rejectAt", ignore = true)
    PurchaseOrderEntity clone(PurchaseOrderEntity entity);
}
