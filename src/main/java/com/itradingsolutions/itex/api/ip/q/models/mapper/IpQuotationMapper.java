package com.itradingsolutions.itex.api.ip.q.models.mapper;

import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationDTO;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationProductDTO;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationsQuoteRequestSummaryDTO;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationEntity;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationProductEntity;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationsQuoteRequestEntity;
import com.itradingsolutions.itex.api.ip.q.models.response.IpQuotationResponse;
import com.itradingsolutions.itex.api.ip.q.models.response.ListIpQuotationResponse;
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

    default IpQuotationsQuoteRequestSummaryDTO map(IpQuotationsQuoteRequestEntity entity) {
        return new IpQuotationsQuoteRequestSummaryDTO(
                entity.getId(),
                entity.getQuoteRequest() != null ? entity.getQuoteRequest().getId() : null,
                entity.getQuoteRequest() != null ? entity.getQuoteRequest().getNumber() : null
        );
    }

    @Mapping(target = "quotationsQuoteRequestId", source = "quotationsQuoteRequest.id")
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
}
