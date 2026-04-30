package com.itradingsolutions.itex.api.partners.clients.models.mappers;

import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientInfoDepDTO;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientInfoDepEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientInfoDepMapper {

    ClientInfoDepDTO entityToDTO(ClientInfoDepEntity entity);
}
