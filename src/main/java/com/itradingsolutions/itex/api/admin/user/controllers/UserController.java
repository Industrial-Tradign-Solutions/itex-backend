package com.itradingsolutions.itex.api.admin.user.controllers;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleOption;
import com.itradingsolutions.itex.api.admin.user.models.dto.UserDTO;
import com.itradingsolutions.itex.api.admin.user.models.responses.ListUserResponse;
import com.itradingsolutions.itex.api.admin.user.models.responses.ListsUsersResponse;
import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.admin.user.models.mappers.UserMapper;
import com.itradingsolutions.itex.api.admin.user.models.requests.ChangePasswordUserRequest;
import com.itradingsolutions.itex.api.admin.user.models.requests.UserRequest;
import com.itradingsolutions.itex.api.common.controller.CommonController;
import com.itradingsolutions.itex.api.common.util.models.enums.HistoryActions;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import com.itradingsolutions.itex.api.admin.user.models.responses.UserResponse;
import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import com.itradingsolutions.itex.config.security.auth.AccessToAction;
import com.itradingsolutions.itex.config.security.auth.AccessToModule;
import com.itradingsolutions.itex.config.websocket.WebSocketMessage;
import com.itradingsolutions.itex.config.websocket.WebSocketMessageValue;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/users")
@Validated
@RequiredArgsConstructor
public class UserController extends CommonController {

        private final IUserService userService;
        private final UserMapper userMapper;

        @GetMapping
        @ResponseStatus(HttpStatus.OK)
        @AccessToModule(option = ModuleOption.USERS)
        public ResponseEntity<List<ListUserResponse>> listAll() {
                var listUsers = userService.listAll();
                return ResponseEntity
                        .ok(listUsers.stream()
                                .map(userMapper::dtoToListResponse)
                                .toList()
                        );
        }

        @GetMapping("/basic")
        @ResponseStatus(HttpStatus.OK)
        public ResponseEntity<ListsUsersResponse> listAllActiveUsers() {
                return ResponseEntity.ok(getListsUsers());
        }

        @GetMapping("/{userId}")
        @ResponseStatus(HttpStatus.OK)
        @AccessToModule(option = ModuleOption.USERS)
        public ResponseEntity<UserResponse> findById(
                @PathVariable UUID userId
        ) {
                var user = userService.findById(userId, false);
                return ResponseEntity.ok(userMapper.dtoToResponse(user));
        }


        @DeleteMapping("/disable/{userId}")
        @ResponseStatus(HttpStatus.OK)
        @AccessToAction(action = ModuleAction.DISABLE_USER)
        public ResponseEntity<MessageResponse<UUID>> disableUser(
                @PathVariable UUID userId
        ) {
                userService.disable(userId);
                sendMessageSocket();
                historyService.saveHistory(HistoryActions.DISABLE, ModuleOption.USERS, userId, null, null);
                return ResponseEntity
                        .ok(
                                new MessageResponse<>(
                                        SUCCESS_TITLE,
                                        "The user was successfully disabled",
                                        userId
                                )
                        );
        }

        @PatchMapping("/enable/{userId}")
        @ResponseStatus(HttpStatus.OK)
        @AccessToAction(action = ModuleAction.ENABLE_USER)
        public ResponseEntity<MessageResponse<UUID>> enableUser(
                @PathVariable UUID userId
        ) {
                userService.enable(userId);
                sendMessageSocket();
                historyService.saveHistory(HistoryActions.ENABLE, ModuleOption.USERS, userId, null, null);
                return ResponseEntity
                        .ok(
                                new MessageResponse<>(
                                        SUCCESS_TITLE,
                                        "The user has been successfully enabled, an e-mail has been sent with the new access data",
                                        userId
                                )
                        );
        }

        @PostMapping("/create")
        @ResponseStatus(HttpStatus.CREATED)
        @AccessToAction(action = ModuleAction.CREATE_USER)
        public ResponseEntity<MessageResponse<UserResponse>> createUser(
                @RequestBody @Valid UserRequest user
        ) {
                var userDTO = userService.create(user);
                sendMessageSocket();
                historyService.saveHistory(HistoryActions.CREATE, ModuleOption.USERS, userDTO.getId(), null, userDTO);
                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(
                                new MessageResponse<>(
                                        SUCCESS_TITLE,
                                        "The user has been successfully created, the access data has been sent to the e-mail address",
                                        userMapper.dtoToResponse(userDTO)
                                )
                        );
        }

        @PutMapping("/update/{userId}")
        @ResponseStatus(HttpStatus.OK)
        @AccessToAction(action = ModuleAction.UPDATE_USER)
        public ResponseEntity<MessageResponse<UserResponse>> updateUser(
                @PathVariable UUID userId,
                @RequestBody @Valid UserRequest user
        ) {
                var oldUser = userService.findById(userId, true);
                var userDto = userService.update(user, userId);
                sendMessageSocket();

                historyService.saveHistory(HistoryActions.UPDATE, ModuleOption.USERS, userId, oldUser, userDto);
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(
                                new MessageResponse<>(
                                        SUCCESS_TITLE,
                                        "The user has been successfully updated",
                                        userMapper.dtoToResponse(userDto)
                                )
                        );
        }

        @PutMapping("/reset-password/{userId}")
        @ResponseStatus(HttpStatus.OK)
        @AccessToAction(action = ModuleAction.RESET_PASS_USER)
        public ResponseEntity<MessageResponse<UUID>> resetPasswordUser(
                @PathVariable UUID userId
        ) {
                userService.resetPassword(userId);
                historyService.saveHistory(HistoryActions.RESET_PASSWORD, ModuleOption.USERS, userId, null, null);
                return ResponseEntity
                        .ok(
                                new MessageResponse<>(
                                        SUCCESS_TITLE,
                                        "The user's password has been reset, an email was sent with the new password.",
                                        userId
                                )
                        );
        }

        @PutMapping("/change-password/{userId}")
        @ResponseStatus(HttpStatus.OK)
        public ResponseEntity<MessageResponse<UserResponse>> changePasswordUser(
                @PathVariable UUID userId,
                @RequestBody @Valid ChangePasswordUserRequest passwordUserRequest
        ) {
                var oldUser = userService.findById(userId, true);
                var userDto = userService.setPassword(passwordUserRequest.getPassword(), passwordUserRequest.getConfirmPassword(), userId);
                historyService.saveHistory(HistoryActions.CHANGE_PASSWORD, ModuleOption.USERS, userId, oldUser, userDto);
                return ResponseEntity
                        .ok(
                                new MessageResponse<>(
                                        SUCCESS_TITLE,
                                        "The password has been successfully updated.",
                                        userMapper.dtoToResponse(userDto)
                                )
                        );
        }

        @DeleteMapping("/close-all-sessions/{offlineMinutes}")
        @ResponseStatus(HttpStatus.OK)
        @AccessToAction(action = ModuleAction.CLOSE_ALL_SESSIONS)
        public ResponseEntity<MessageResponse<String>> closeAllSessions(@PathVariable int offlineMinutes) {
                userService.closeAllSessions(offlineMinutes);
                historyService.saveHistory(HistoryActions.CLOSE_ALL_SESSIONS, ModuleOption.USERS, null, null, null);
                return ResponseEntity
                        .ok(
                                new MessageResponse<>(
                                        SUCCESS_TITLE,
                                        "Notification for logout has been sent to all users",
                                        ""
                                )
                        );
        }

        @GetMapping(value= "/{userId}/photo-profile", produces = MediaType.IMAGE_JPEG_VALUE)
        @ResponseStatus(HttpStatus.OK)
        public ResponseEntity<byte[]> getProfilePhoto(@PathVariable UUID userId) {
            var user = userService.findById(userId, true);
            String path = user.getPhotoUrl();
            File file = (path != null) ? new File(path) : null;
            byte[] bytes;
            String contentType;
            try {
                if (file != null && file.exists()) {
                    bytes = Files.readAllBytes(file.toPath());
                    contentType = Files.probeContentType(file.toPath());
                } else {
                    ClassPathResource resource = new ClassPathResource("images/avatar.avif");
                    bytes = StreamUtils.copyToByteArray(resource.getInputStream());
                    contentType = "image/avif";
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(bytes);
        }

        private ListsUsersResponse getListsUsers() {
                var listUsers = userService.listAll();
                return new ListsUsersResponse(
                        listUsers.stream().filter(UserDTO::getActive).map(userMapper::dtoToBasicResponse).toList(),
                        listUsers.stream().filter(user -> !user.getActive()).map(user -> {
                                user.setFullName(user.getFullName() + " (DISABLED)");
                                return userMapper.dtoToBasicResponse(user);
                        }).toList()
                );
        }

        private void sendMessageSocket() {
                new Thread(() -> socketHandler.sendMessage(new WebSocketMessage<>(getListsUsers(), WebSocketMessageValue.LIST_USERS))).start();
        }

}
