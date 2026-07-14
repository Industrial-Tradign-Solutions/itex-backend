package com.itradingsolutions.itex.api.ip.po.models.mapper;

import com.itradingsolutions.itex.api.ip.po.models.dto.IpPurchaseOrderDTO;
import com.itradingsolutions.itex.api.ip.po.models.dto.IpPurchaseOrderOtherChargeDTO;
import com.itradingsolutions.itex.api.ip.po.models.dto.IpPurchaseOrderOtherChargesQuotationDTO;
import com.itradingsolutions.itex.api.ip.po.models.dto.IpPurchaseOrderOtherChargesQuotationQrDTO;
import com.itradingsolutions.itex.api.ip.po.models.dto.IpPurchaseOrderProductDTO;
import com.itradingsolutions.itex.api.ip.po.models.entities.IpPurchaseOrderEntity;
import com.itradingsolutions.itex.api.ip.po.models.request.CreateIpPurchaseOrderRequest;
import com.itradingsolutions.itex.api.ip.po.models.response.IpPurchaseOrderOtherChargeResponse;
import com.itradingsolutions.itex.api.ip.po.models.response.IpPurchaseOrderOtherChargesQuotationResponse;
import com.itradingsolutions.itex.api.ip.po.models.response.IpPurchaseOrderOtherChargesQuotationQrResponse;
import com.itradingsolutions.itex.api.ip.po.models.response.IpPurchaseOrderProductResponse;
import com.itradingsolutions.itex.api.ip.po.models.response.IpPurchaseOrderResponse;
import com.itradingsolutions.itex.api.ip.po.models.response.ListIpPurchaseOrderResponse;
import com.itradingsolutions.itex.api.ip.q.models.mapper.IpQuotationOtherChargeMapper;
import com.itradingsolutions.itex.api.ip.q.models.mapper.IpQuotationOtherChargesQuoteRequestMapper;
import com.itradingsolutions.itex.api.ip.q.models.mapper.IpQuotationProductMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {IpPurchaseOrderProductMapper.class, IpPurchaseOrderOtherChargeMapper.class,
                IpPurchaseOrderOtherChargesQuotationMapper.class, IpPurchaseOrderOtherChargesQuotationQrMapper.class,
                IpQuotationProductMapper.class, IpQuotationOtherChargeMapper.class,
                IpQuotationOtherChargesQuoteRequestMapper.class})
public interface IpPurchaseOrderMapper {

    IpPurchaseOrderDTO entityToDTO(IpPurchaseOrderEntity entity);

    @Mapping(target = "products", expression = "java(mapProductsResponse(dto))")
    @Mapping(target = "otherCharges", expression = "java(mapOtherChargesResponse(dto))")
    @Mapping(target = "importedQuotationCharges", expression = "java(mapImportedQCharges(dto))")
    @Mapping(target = "importedQuoteRequestCharges", expression = "java(mapImportedQrCharges(dto))")
    IpPurchaseOrderResponse dtoToResponse(IpPurchaseOrderDTO dto);

    ListIpPurchaseOrderResponse dtoToListResponse(IpPurchaseOrderDTO dto);

    default List<IpPurchaseOrderProductResponse> mapProductsResponse(IpPurchaseOrderDTO dto) {
        if (dto.getProducts() == null) return Collections.emptyList();
        return dto.getProducts().stream()
                .map(this::toProductResponse)
                .toList();
    }

    IpPurchaseOrderProductResponse toProductResponse(IpPurchaseOrderProductDTO dto);

    default List<IpPurchaseOrderOtherChargeResponse> mapOtherChargesResponse(IpPurchaseOrderDTO dto) {
        if (dto.getOtherCharges() == null) return Collections.emptyList();
        return dto.getOtherCharges().stream()
                .map(this::toOtherChargeResponse)
                .toList();
    }

    IpPurchaseOrderOtherChargeResponse toOtherChargeResponse(IpPurchaseOrderOtherChargeDTO dto);

    default List<IpPurchaseOrderOtherChargesQuotationResponse> mapImportedQCharges(IpPurchaseOrderDTO dto) {
        if (dto.getImportedQuotationCharges() == null) return Collections.emptyList();
        return dto.getImportedQuotationCharges().stream()
                .map(this::toImportedQChargeResponse)
                .toList();
    }

    IpPurchaseOrderOtherChargesQuotationResponse toImportedQChargeResponse(IpPurchaseOrderOtherChargesQuotationDTO dto);

    default List<IpPurchaseOrderOtherChargesQuotationQrResponse> mapImportedQrCharges(IpPurchaseOrderDTO dto) {
        if (dto.getImportedQuoteRequestCharges() == null) return Collections.emptyList();
        return dto.getImportedQuoteRequestCharges().stream()
                .map(this::toImportedQrChargeResponse)
                .toList();
    }

    IpPurchaseOrderOtherChargesQuotationQrResponse toImportedQrChargeResponse(IpPurchaseOrderOtherChargesQuotationQrDTO dto);

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
    IpPurchaseOrderEntity clone(IpPurchaseOrderEntity entity);
}
