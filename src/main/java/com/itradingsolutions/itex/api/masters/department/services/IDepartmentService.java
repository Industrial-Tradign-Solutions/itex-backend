package com.itradingsolutions.itex.api.masters.department.services;

import com.itradingsolutions.itex.api.common.service.CommonService;
import com.itradingsolutions.itex.api.masters.department.models.dto.DepartmentDTO;
import com.itradingsolutions.itex.api.masters.department.models.entities.DepartmentEntity;

import java.util.List;
import java.util.UUID;

public interface IDepartmentService extends CommonService<DepartmentDTO> {

    List<DepartmentDTO> listAllShowInfo(boolean isShowOnlyClientInfo, boolean isShowOnlySupplierInfo);
    DepartmentEntity findEntityById(UUID id, boolean validateActive);
}
