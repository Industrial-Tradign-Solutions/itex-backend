package com.itradingsolutions.itex.api.admin.role.models.mappers;

import com.itradingsolutions.itex.api.admin.role.models.dto.ActionDTO;
import com.itradingsolutions.itex.api.admin.role.models.dto.RoleActionDTO;
import com.itradingsolutions.itex.api.admin.role.models.entities.ActionEntity;
import com.itradingsolutions.itex.api.admin.role.models.entities.RoleActionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleActionMapper {
    List<ActionDTO> actionEntitiesToActions(List<ActionEntity> roleActionEntities);
    ActionDTO actionEntityToAction(ActionEntity actionEntity);
    List<RoleActionDTO> roleActionEntitiesToRoleAction(List<RoleActionEntity> roleActionEntities);

}
