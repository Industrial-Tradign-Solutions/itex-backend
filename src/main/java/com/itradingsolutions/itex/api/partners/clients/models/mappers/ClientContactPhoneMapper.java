package com.itradingsolutions.itex.api.partners.clients.models.mappers;

import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientContactPhoneDTO;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientContactPhoneEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientContactPhoneMapper {
    ClientContactPhoneDTO entityToDTO(ClientContactPhoneEntity entity);
}
