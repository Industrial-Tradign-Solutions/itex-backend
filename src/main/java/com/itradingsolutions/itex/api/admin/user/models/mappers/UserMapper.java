package com.itradingsolutions.itex.api.admin.user.models.mappers;

import com.itradingsolutions.itex.api.admin.user.models.dto.UserDTO;
import com.itradingsolutions.itex.api.admin.user.models.dto.UserDetailDTO;
import com.itradingsolutions.itex.api.admin.user.models.entities.UserDepartmentEntity;
import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import com.itradingsolutions.itex.api.admin.user.models.responses.BasicUserResponse;
import com.itradingsolutions.itex.api.admin.user.models.responses.ListUserResponse;
import com.itradingsolutions.itex.api.admin.user.models.responses.UserResponse;
import com.itradingsolutions.itex.api.masters.department.models.entities.DepartmentEntity;
import com.itradingsolutions.itex.api.masters.department.models.responses.BasicDepartmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDetailDTO entityToDetailDTO(UserEntity entity);

    UserDTO entityToDTO(UserEntity entity);
    UserResponse dtoToResponse(UserDTO dto);
    ListUserResponse dtoToListResponse(UserDTO dto);

    BasicUserResponse dtoToBasicResponse(UserDTO dto);

    default BasicDepartmentResponse map(UserDepartmentEntity value) {
        DepartmentEntity department = value.getDepartment();
        if (department == null) return null;
        return BasicDepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .active(department.isActive())
                .build();
    }
}
