package com.itradingsolutions.itex.api.common.email.controller;

import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import com.itradingsolutions.itex.api.common.controller.CommonController;
import com.itradingsolutions.itex.api.common.email.model.request.EmailRequest;
import com.itradingsolutions.itex.api.common.email.service.IMailService;
import com.itradingsolutions.itex.api.common.util.models.responses.MessageResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/email")
@Validated
@AllArgsConstructor
public class EmailController extends CommonController {

    private final IMailService mailService;
    private final IUserService userService;

    @PostMapping("/sent")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MessageResponse<String>> sendEmail(
            @RequestBody @Valid EmailRequest email
    ) {
        var user = userService.getUserAuthenticatedDto();
        mailService.sendEmail(email, user);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponse<>(
                                SUCCESS_TITLE,
                                simpleMessage("email.send"),
                                user.getEmail()
                        )
                );
    }

    @PostMapping(value = "/send-attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MessageResponse<String>> sendEmailAttachment(
            @RequestPart("request") EmailRequest email,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        var user = userService.getUserAuthenticatedDto();
        mailService.sendEmailAttachments(email, files, user);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new MessageResponse<>(
                                SUCCESS_TITLE,
                                simpleMessage("email.send"),
                                user.getEmail()
                        )
                );
    }
}
