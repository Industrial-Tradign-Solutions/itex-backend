package com.itradingsolutions.itex.api.partners.clients.models.mappers;

import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientDTO;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientEntity;
import com.itradingsolutions.itex.api.partners.clients.models.responses.BasicClientResponse;
import com.itradingsolutions.itex.api.partners.clients.models.responses.ClientResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientMapper {
    ClientDTO entityToDto(ClientEntity entity);
    ClientResponse dtoToResponse(ClientDTO dto);
    BasicClientResponse dtoToBasicResponse(ClientDTO dto);
}
