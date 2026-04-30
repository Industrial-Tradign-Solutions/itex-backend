package com.itradingsolutions.itex.api.admin.role.models.mappers;

import com.itradingsolutions.itex.api.admin.role.models.dto.RoleDTO;
import com.itradingsolutions.itex.api.admin.role.models.entities.RoleEntity;

import com.itradingsolutions.itex.api.admin.role.models.requests.RoleRequest;
import com.itradingsolutions.itex.api.admin.role.models.responses.BasicRoleResponse;
import com.itradingsolutions.itex.api.admin.role.models.responses.RoleResponse;
import com.itradingsolutions.itex.api.masters.common.models.mappers.CommonMasterMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper extends CommonMasterMapper<RoleDTO, BasicRoleResponse, RoleResponse, RoleRequest> {

    RoleDTO entityToDto(RoleEntity entity);

}
