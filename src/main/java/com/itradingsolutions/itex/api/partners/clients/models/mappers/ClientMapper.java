package com.itradingsolutions.itex.api.partners.clients.models.mappers;

import com.itradingsolutions.itex.api.admin.user.models.mappers.UserMapper;
import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientDTO;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientEntity;
import com.itradingsolutions.itex.api.partners.clients.models.responses.BasicClientResponse;
import com.itradingsolutions.itex.api.partners.clients.models.responses.ClientResponse;
import com.itradingsolutions.itex.api.partners.clients.models.responses.ListClientResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = UserMapper.class)
public interface ClientMapper {
    ClientDTO entityToDto(ClientEntity entity);
    ClientResponse dtoToResponse(ClientDTO dto);
    BasicClientResponse dtoToBasicResponse(ClientDTO dto);

    @Mapping(target = "city", expression = "java(entity.getCity() != null ? entity.getCity().getFullName() : null)")
    ListClientResponse entityToListResponse(ClientEntity entity);

    @Mapping(target = "showName", expression = "java(\"(\" + entity.getCode() + \") \" + entity.getName())")
    BasicClientResponse entityToBasicResponse(ClientEntity entity);
}
