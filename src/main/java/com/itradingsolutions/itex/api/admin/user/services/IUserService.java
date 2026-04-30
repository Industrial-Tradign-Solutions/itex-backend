package com.itradingsolutions.itex.api.admin.user.services;

import com.itradingsolutions.itex.api.admin.user.models.dto.UserDTO;
import com.itradingsolutions.itex.api.admin.user.models.dto.UserDetailDTO;
import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import com.itradingsolutions.itex.api.admin.user.models.requests.UserRequest;

import java.util.List;
import java.util.UUID;

public interface IUserService {

    UserDetailDTO findDetailByUsername(String username);
    UserEntity getUserAuthenticated();
    UserEntity getUserByUsername(String username);


    UserDTO getUserAuthenticatedDto();
    UserEntity findEntityById(UUID userId, boolean validateActive);
    UserDTO findById(UUID userId, boolean validateActive);
    UserDTO create(UserRequest user);
    UserDTO update(UserRequest user, UUID userId);

    List<UserDTO> listAllActive();
    List<UserDTO> listAll();
    void disable(UUID userId);
    void enable(UUID userId);
    void resetPassword(UUID userId);
    void resetPasswordSchedule(UUID userId);
    UserDTO setPassword(String password, String confirmPassword, UUID userId);
    void closeAllSessions(int offlineMinutes);
}
