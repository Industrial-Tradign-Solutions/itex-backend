package com.itradingsolutions.itex.api.common.email.service;

import com.itradingsolutions.itex.api.admin.user.models.dto.UserDTO;
import com.itradingsolutions.itex.api.common.email.model.request.EmailRequest;
import com.itradingsolutions.itex.api.common.email.model.enums.MailTemplates;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface IMailService {
    void sendBasic(String to, String subject, String body, boolean isNecessaryWait);
    void sendTemplate(String to, String subject, Map<String, Object> templateModel, boolean isNecessaryWait, MailTemplates template);
    void sendEmail(EmailRequest email, UserDTO user);
    void sendEmailAttachments(EmailRequest email, List<MultipartFile> attachments, UserDTO user);
}
