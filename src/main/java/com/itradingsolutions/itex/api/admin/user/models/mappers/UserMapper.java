package com.itradingsolutions.itex.api.admin.user.models.mappers;

import com.itradingsolutions.itex.api.admin.user.models.dto.UserDTO;
import com.itradingsolutions.itex.api.admin.user.models.dto.UserDetailDTO;
import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import com.itradingsolutions.itex.api.admin.user.models.responses.BasicUserResponse;
import com.itradingsolutions.itex.api.admin.user.models.responses.ListUserResponse;
import com.itradingsolutions.itex.api.admin.user.models.responses.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserDetailDTO entityToDetailDTO(UserEntity entity);

    UserDTO entityToDTO(UserEntity entity);
    UserResponse dtoToResponse(UserDTO dto);
    ListUserResponse dtoToListResponse(UserDTO dto);

    BasicUserResponse dtoToBasicResponse(UserDTO dto);
}
