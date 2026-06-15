package com.itradingsolutions.itex.api.ip.q.models.mapper;

import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationDTO;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationProductDTO;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationsQuoteRequestSummaryDTO;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationEntity;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationProductEntity;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationsQuoteRequestEntity;
import com.itradingsolutions.itex.api.ip.q.models.response.IpQuotationResponse;
import com.itradingsolutions.itex.api.ip.q.models.response.ListIpQuotationResponse;
import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestEntity;
import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IpQuotationMapper {

    ListIpQuotationResponse dtoToListResponse(IpQuotationDTO dto);

    @Mapping(target = "listQuoteRequests", source = "quoteRequestsQuotations")
    @Mapping(target = "products", expression = "java(mapProducts(entity))")
    IpQuotationDTO entityToDTO(IpQuotationEntity entity);

    IpQuotationResponse dtoToResponse(IpQuotationDTO dto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "number", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "quoteRequestsQuotations", ignore = true)
    @Mapping(target = "pdfUrl", ignore = true)
    @Mapping(target = "openBy", ignore = true)
    @Mapping(target = "openAt", ignore = true)
    @Mapping(target = "sentAt", ignore = true)
    @Mapping(target = "answeredAt", ignore = true)
    @Mapping(target = "completeAt", ignore = true)
    @Mapping(target = "rejectAt", ignore = true)
    @Mapping(target = "clonedQuotations", ignore = true)
    IpQuotationEntity clone(IpQuotationEntity source);

    default IpQuotationsQuoteRequestSummaryDTO map(IpQuotationsQuoteRequestEntity entity) {
        return new IpQuotationsQuoteRequestSummaryDTO(
                entity.getId(),
                entity.getQuoteRequest() != null ? entity.getQuoteRequest().getId() : null,
                entity.getQuoteRequest() != null ? entity.getQuoteRequest().getNumber() : null
        );
    }

    @Mapping(target = "quotationsQuoteRequestId", source = "quotationsQuoteRequest.id")
    @Mapping(target = "qrNumber", expression = "java(mapQrNumberFromProduct(entity))")
    @Mapping(target = "supplierName", expression = "java(mapSupplierNameFromProduct(entity))")
    IpQuotationProductDTO mapProduct(IpQuotationProductEntity entity);

    default List<IpQuotationProductDTO> mapProducts(IpQuotationEntity entity) {
        if (entity.getQuoteRequestsQuotations() == null) return Collections.emptyList();
        return entity.getQuoteRequestsQuotations().stream()
                .filter(qqr -> qqr.getQuotationProducts() != null)
                .flatMap(qqr -> qqr.getQuotationProducts().stream())
                .sorted(Comparator.comparingInt(p -> p.getNumber() != null ? p.getNumber() : 0))
                .map(this::mapProduct)
                .toList();
    }

    default String mapQrNumberFromProduct(IpQuotationProductEntity entity) {
        if (entity == null) return null;
        var qqr = entity.getQuotationsQuoteRequest();
        if (qqr == null) return null;
        IpQuoteRequestEntity qr = qqr.getQuoteRequest();
        if (qr == null) return null;
        return qr.getNumber();
    }

    default String mapSupplierNameFromProduct(IpQuotationProductEntity entity) {
        if (entity == null) return null;
        var qqr = entity.getQuotationsQuoteRequest();
        if (qqr == null) return null;
        IpQuoteRequestEntity qr = qqr.getQuoteRequest();
        if (qr == null) return null;
        SupplierEntity supplier = qr.getSupplier();
        if (supplier == null) return null;
        return supplier.getName();
    }
}
