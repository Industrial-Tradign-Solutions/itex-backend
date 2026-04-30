package com.itradingsolutions.itex.api.masters.department.models.mappers;

import com.itradingsolutions.itex.api.masters.common.models.mappers.CommonMasterMapper;
import com.itradingsolutions.itex.api.masters.department.models.dto.DepartmentDTO;
import com.itradingsolutions.itex.api.masters.department.models.entities.DepartmentEntity;
import com.itradingsolutions.itex.api.masters.department.models.requests.DepartmentRequest;
import com.itradingsolutions.itex.api.masters.department.models.responses.BasicDepartmentResponse;
import com.itradingsolutions.itex.api.masters.department.models.responses.DepartmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DepartmentMapper extends CommonMasterMapper<DepartmentDTO, BasicDepartmentResponse, DepartmentResponse, DepartmentRequest> {

    DepartmentDTO entityToDto(DepartmentEntity entity);
}
