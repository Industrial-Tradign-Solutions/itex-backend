package com.itradingsolutions.itex.api.admin.user.services.impl;

import com.itradingsolutions.itex.api.admin.user.models.entities.UserDepartmentEntity;
import com.itradingsolutions.itex.api.admin.user.models.entities.UserDepartmentEntityId;
import com.itradingsolutions.itex.api.admin.user.repositories.IUserDepartmentRepository;
import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;
import com.itradingsolutions.itex.api.admin.user.exceptions.NotEqualPasswordException;
import com.itradingsolutions.itex.api.admin.user.exceptions.NotExistUserException;
import com.itradingsolutions.itex.api.admin.user.exceptions.NotUserActiveException;
import com.itradingsolutions.itex.api.admin.user.models.dto.UserDTO;
import com.itradingsolutions.itex.api.admin.user.models.dto.UserDetailDTO;
import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import com.itradingsolutions.itex.api.admin.user.models.mappers.UserMapper;
import com.itradingsolutions.itex.api.admin.user.models.requests.UserRequest;
import com.itradingsolutions.itex.api.admin.user.repositories.IUserRepository;
import com.itradingsolutions.itex.api.admin.role.services.IRoleService;
import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import com.itradingsolutions.itex.api.common.email.model.enums.MailTemplates;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.masters.department.services.IDepartmentService;
import com.itradingsolutions.itex.api.common.email.service.IMailService;
import com.itradingsolutions.itex.config.websocket.WebSocketHandlerItex;
import com.itradingsolutions.itex.config.websocket.WebSocketMessage;
import com.itradingsolutions.itex.config.websocket.WebSocketMessageValue;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserServiceIml extends UtilServiceAbs implements IUserService {

    private final IUserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;
    private final WebSocketHandlerItex socketHandler;
    private final IRoleService roleService;
    private final IDepartmentService departmentService;
    private final IUserDepartmentRepository userDepartmentRepository;
    private final IMailService mailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${itex.url.web}")
    private String webUrl;

    @Override
    @Transactional(readOnly = true)
    public UserDetailDTO findDetailByUsername(String username) {
        var user = getUserByUsername(username, false);
        return userMapper.entityToDetailDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity getUserAuthenticated() {
        return getUserByUsername(getAuthenticatedUsername(), true);
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity getUserByUsername(String username) {
        return getUserByUsername(username, true);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserAuthenticatedDto() {
        return userMapper.entityToDTO(getUserByUsername(getAuthenticatedUsername(), true));
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity findEntityById(UUID userId, boolean validateActive) {
        return getUserById(userId, validateActive);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO findById(UUID userId, boolean validateActive) {
        return userMapper.entityToDTO(getUserById(userId, validateActive));
    }

    @Override
    @Transactional
    public UserDTO create(UserRequest user) {
        UserEntity userEntity = new UserEntity();
        userEntity.setActive(true);
        userEntity.setPassChanged(false);
        var password = generatePassword();
        userEntity.setPass(encoder.encode(password));
        userEntity = userRepository.save(createOrUpdate(user, userEntity));
        userEntity.setDepartments(saveDepartments(user.getDepartmentsIds(), userEntity));
        sendEmailConfirmationUser(userEntity, password, false);
        return userMapper.entityToDTO(userEntity);
    }

    private List<UserDepartmentEntity> saveDepartments(List<UUID> userDepartments, UserEntity userEntity) {
        List<UserDepartmentEntity> resp = new ArrayList<>();

        if (userEntity.getDepartments() != null && !userEntity.getDepartments().isEmpty()) {
            var deleteItems = userEntity.getDepartments()
                    .stream().filter(e ->
                            userDepartments
                                    .stream().noneMatch(r -> r != null && r.equals(e.getDepartment().getId()))
                    ).toList();
            deleteItems.forEach(e -> userDepartmentRepository.deleteDepByUserId(userEntity.getId(), e.getDepartment().getId()));
        }

        for (UUID depId: userDepartments) {
            UserDepartmentEntity newUserDepartment = new UserDepartmentEntity();
            newUserDepartment.setUser(userEntity);
            newUserDepartment.setDepartment(departmentService.findEntityById(depId, true));
            newUserDepartment.setId(new UserDepartmentEntityId());
            newUserDepartment.getId().setIdDepartment(depId);
            newUserDepartment.getId().setIdUser(userEntity.getId());
            resp.add(userDepartmentRepository.save(newUserDepartment));
        }

        return resp;
    }



    @Override
    @Transactional
    public UserDTO update(UserRequest user, UUID userId) {
        var userEntity = getUserById(userId, true);
        userEntity = userRepository.save(createOrUpdate(user, userEntity));
        userEntity.setDepartments(saveDepartments(user.getDepartmentsIds(), userEntity));
        return userMapper.entityToDTO(userEntity);
    }

    private UserEntity createOrUpdate(UserRequest request, UserEntity entity) {
        entity.setName(normalizeText(capitalizeName(request.getName())));
        entity.setLastName(normalizeText(capitalizeName(request.getLastName())));
        entity.setEmail(request.getEmail().toLowerCase().trim());
        entity.setTitle(normalizeText(request.getTitle()).toUpperCase().trim());
        entity.setExtension(request.getExtension().toLowerCase().trim());
        entity.setUser(normalizeText(request.getUser()).toLowerCase().trim());

        if (request.getEmailPassword() != null && !request.getEmailPassword().isBlank()) {
            if (!request.getEmailPassword().equals(entity.getEmailPassword()))
                entity.setEmailPassword(encryptText(request.getEmailPassword()));
            else
                entity.setEmailPassword(request.getEmailPassword());
        } else {
            entity.setEmailPassword(null);
        }

        if (entity.getRole() == null || !entity.getRole().getId().equals(request.getRoleId()))
            entity.setRole(roleService.findEntityById(request.getRoleId(), true));

        return entity;
    }


    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> listAllActive() {
        var listActiveUsers = userRepository.fetchAllActive();
        if (listActiveUsers == null || listActiveUsers.isEmpty()) {
            throw new NotUserActiveException("There are no active users");
        }
        return listActiveUsers.stream().map(userMapper::entityToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> listAll() {
        return userRepository.fetchAll().stream().map(userMapper::entityToDTO).toList();
    }

    @Override
    @Transactional
    public void disable(UUID userId) {
        var userEntity = getUserById(userId, true);
        userEntity.setActive(false);
        userRepository.save(userEntity);
        new Thread(() -> socketHandler.closeSessionUser(null, userId, WebSocketMessageValue.DISABLE_USER)).start();
    }

    @Override
    @Transactional
    public void enable(UUID userId) {
        var userEntity = getUserById(userId, false);
        if (userEntity.isActive())
            throw new NotUserActiveException("The user is active");
        userEntity.setActive(true);
        userEntity.setPassChanged(false);
        var password = generatePassword();
        userEntity.setPass(encoder.encode(password));
        sendEmailConfirmationUser(userRepository.save(userEntity), password, true);
    }

    @Override
    @Transactional
    public void resetPassword(UUID userId) {
        resetPasswordAction(userId,true);
    }

    @Override
    @Transactional
    public void resetPasswordSchedule(UUID userId) {
        resetPasswordAction(userId,false);
    }

    private void resetPasswordAction(UUID userId, boolean isChangPassword) {
        var userEntity = getUserById(userId, true);

        if (!userEntity.isPassChanged())
            throw new NotUserActiveException("The user cannot reset the password");
        String password;
        userEntity.setPassChanged(false);
        if (isChangPassword) {
            password = generatePassword();
            userEntity.setPass(encoder.encode(password));
            sendEmailConfirmationUser(userRepository.save(userEntity), password, true);
        } else {
            userRepository.save(userEntity);
        }
    }

    @Override
    @Transactional
    public UserDTO setPassword(String password, String confirmPassword, UUID userId) {
        var userEntity = getUserById(userId, true);
        if (userEntity.isPassChanged())
            throw new NotUserActiveException("The user cannot update the password");
        if (passwordEncoder.matches(password, userEntity.getPass()))
            throw new NotEqualPasswordException("Your password cannot be the same as before");

        userEntity.setPassChanged(true);
        userEntity.setPassChangedAt(ZonedDateTime.now(zoneId));
        userEntity.setPass(validatePassword(password, confirmPassword));
        return userMapper.entityToDTO(userRepository.save(userEntity));
    }

    @Override
    public void closeAllSessions(int offlineMinutes) {
        int timeout = 300000;
        int period  = 30000;

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            private int count = 0;

            @Override
            public void run() {
                int maxCount = 10;
                if (count < maxCount) {
                    int countMills = count * period;
                    float minutes = (float) (timeout - countMills) / 60000;
                    socketHandler.sendMessage(new WebSocketMessage<>("The system will be offline in [" + minutes +"] minutes", WebSocketMessageValue.NOTIFICATION_LOGOUT));
                    count++;
                } else {
                    timer.cancel();
                }
            }
        };
        timer.schedule(task, 0, period);
        new Thread(() -> {
            try {
              Thread.sleep(timeout);
              socketHandler.sendMessage(new WebSocketMessage<>("Your session has ended, the system will be offline for " + offlineMinutes +" minutes;", WebSocketMessageValue.CLOSE_ALL_SESSIONS));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new NotFoundException("Error when sending the logout notification", e);
            }
        }).start();
    }


    private UserEntity getUserById(UUID id, boolean validateActive){
        var user = userRepository.findById(id)
                .orElseThrow(() ->
                        new NotExistUserException("The consulted user does not exist")
                );
        validateUserActive(validateActive, user);
        return user;
    }

    private UserEntity getUserByUsername(String username, boolean validateActive) {
        var user = userRepository.findByUser(username)
                .orElseThrow(() ->
                        new NotExistUserException("The consulted user does not exist")
                );
        validateUserActive(validateActive, user);
        return user;
    }

    private void validateUserActive(boolean validateActive, UserEntity user) {
        if(validateActive && (!user.isActive()))
            throw new NotUserActiveException("The user " + user.getFullName() + " is not active");
    }



    private String validatePassword(String password, String confirmPassword) {
        if (!password.equals(confirmPassword))
            throw new NotEqualPasswordException("Password entered is not the same as confirmation");
        return encoder.encode(password);
    }

    private String generatePassword() {
        final int lengthText = 10;
        final String allowedCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        SecureRandom random = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder(lengthText);

        for (int i = 0; i < lengthText; i++) {
            int index = random.nextInt(allowedCharacters.length());
            char character = allowedCharacters.charAt(index);
            stringBuilder.append(character);
        }

        return stringBuilder.toString();
    }

    private void sendEmailConfirmationUser(UserEntity user, String password, boolean isReset) {
        String mailTo = user.getEmail();
        String subject = "User Created";
        String message = "A user has been created for the ITEX platform, its credentials are as follows";

        if (isReset) {
            subject = "Reset Password";
            message = "Your access password to the ITEX platform has been reset, your credentials are as follows";
        }

        Map<String, Object> data = new HashMap<>();
        data.put("username", user.getUser());
        data.put("password", password);
        data.put("name", user.getFullName());
        data.put("url", webUrl);
        data.put("message", message);

        mailService.sendTemplate(mailTo, subject, data, false, MailTemplates.REGISTER_USER);
    }
}
