package com.itradingsolutions.itex.api.ip.q.models.mapper;

import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationOtherChargeDTO;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationOtherChargeEntity;
import com.itradingsolutions.itex.api.ip.q.models.request.IpQuotationOtherChargeRequest;
import com.itradingsolutions.itex.api.ip.q.models.response.IpQuotationOtherChargeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for converting between Other Charge entities, DTOs, requests, and responses.
 * <p>
 * Provides bidirectional mapping between the persistence layer (Entity) and the application layer (DTO).
 * Also supports cloning entities for quotation duplication.
 * </p>
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface IpQuotationOtherChargeMapper {

    /**
     * Converts a request object to a DTO.
     *
     * @param request the incoming request with other charge data
     * @return the DTO representation
     */
    IpQuotationOtherChargeDTO requestToDTO(IpQuotationOtherChargeRequest request);

    /**
     * Converts an entity to a DTO.
     *
     * @param entity the JPA entity
     * @return the DTO representation
     */
    IpQuotationOtherChargeDTO entityToDto(IpQuotationOtherChargeEntity entity);

    /**
     * Clones an entity for quotation cloning operations.
     * <p>
     * Ignores id, ipQuotation, and createdAt to create a fresh entity for the new quotation.
     * </p>
     *
     * @param entity the original entity to clone
     * @return a new entity with the same value and description
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ipQuotation", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    IpQuotationOtherChargeEntity clone(IpQuotationOtherChargeEntity entity);

    /**
     * Converts a DTO to a response object.
     *
     * @param dto the DTO
     * @return the response representation for API consumers
     */
    IpQuotationOtherChargeResponse dtoToResponse(IpQuotationOtherChargeDTO dto);
}
