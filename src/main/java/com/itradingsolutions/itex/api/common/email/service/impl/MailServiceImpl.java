package com.itradingsolutions.itex.api.common.email.service.impl;

import com.itradingsolutions.itex.api.admin.user.models.dto.UserDTO;
import com.itradingsolutions.itex.api.common.email.model.request.EmailRequest;
import com.itradingsolutions.itex.api.common.util.exceptions.NotSendEmailException;
import com.itradingsolutions.itex.api.common.email.model.enums.MailTemplates;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.common.email.service.IMailService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MailServiceImpl extends UtilServiceAbs implements IMailService {

    private final JavaMailSenderImpl emailSender;
    private final Configuration freemarkerConfig;

    @Value("${spring.mail.username}")
    private String mailFrom;

    @Value("${spring.mail.password}")
    private String mailFromPass;

    private static final String TEMPLATE_ERROR_MESSAGE = "emails.error.send";

    @Override
    public void sendBasic(String to, String subject, String body, boolean isNecessaryWait) {
        if (isNecessaryWait) {
            sendBasicAction(to, subject, body);
        } else {
            new Thread(() -> sendBasicAction(to, subject, body)).start();
        }
    }

    @Override
    public void sendTemplate(String to, String subject, Map<String, Object> templateModel, boolean isNecessaryWait, MailTemplates template) {
        if (isNecessaryWait) {
            sendBasicMailTemplate(to, subject, templateModel, template);
        } else {
            new Thread(() -> sendBasicMailTemplate(to, subject, templateModel, template)).start();
        }
    }

    @Override
    public void sendEmail(EmailRequest email, UserDTO user) {
        sendEmailAction(email, null, user);
    }

    @Override
    public void sendEmailAttachments(EmailRequest email, List<MultipartFile> attachments, UserDTO user) {
        sendEmailAction(email, attachments, user);
    }

    private void sendEmailAction(EmailRequest email, List<MultipartFile> attachments, UserDTO user) {
        if ((user.getEmail() == null || user.getEmail().isBlank()) || (user.getEmailPassword() == null || user.getEmailPassword().isBlank()))
            throw new NotSendEmailException(simpleMessage(TEMPLATE_ERROR_MESSAGE));
        var emailPassword = decryptText(user.getEmailPassword());
        try {
            emailSender.setPassword(emailPassword);
            emailSender.setUsername(user.getEmail());
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            Template template = getTemplate(MailTemplates.COMMERCIAL);

            Map<String, Object> templateModel = new HashMap<>();
            templateModel.put("body", email.body());
            templateModel.put("name", user.getFullName().toUpperCase());
            templateModel.put("title", user.getTitle());
            templateModel.put("email", user.getEmail());
            templateModel.put("ext", user.getExtension());

            String htmlContent = FreeMarkerTemplateUtils.processTemplateIntoString(template, templateModel);

            helper.setFrom(user.getFullName() + " - ITS <" + user.getEmail() + ">");
            helper.setTo(email.to().toArray(new String[0]));
            if (email.cc() != null)
                helper.setCc(email.cc().toArray(new String[0]));
            if (email.cco() != null)
                helper.setBcc(email.cco().toArray(new String[0]));

            helper.setText(htmlContent, true);
            helper.addInline("logoId", new ClassPathResource("images/logo.png"));
            helper.addInline("iconInstagram", new ClassPathResource("images/instagram.png"));
            helper.addInline("iconFacebook", new ClassPathResource("images/facebook.png"));
            helper.addInline("iconLinkedin", new ClassPathResource("images/linkedin.png"));
            helper.addInline("iconAddress", new ClassPathResource("images/gps.png"));
            helper.addInline("iconWeb", new ClassPathResource("images/web.png"));
            helper.addInline("iconPhone", new ClassPathResource("images/phone.png"));
            helper.addInline("iconEmail", new ClassPathResource("images/email.png"));

            if (user.getPhotoUrl() == null || user.getPhotoUrl().isBlank()) {
                helper.addInline("profile", new ClassPathResource("images/avatar.avif"));
            } else {
                helper.addInline("profile", new FileSystemResource(user.getPhotoUrl()));
            }

            if (attachments != null) {
                for (MultipartFile file : attachments) {
                    if (!file.isEmpty()) {
                        helper.addAttachment(Objects.requireNonNull(file.getOriginalFilename()), file);
                    }
                }
            }

            helper.setSubject(email.subject());
            sendAndRestoreMail(message);
        } catch (TemplateException | MessagingException | IOException ex) {
            throw new NotSendEmailException(simpleMessage(TEMPLATE_ERROR_MESSAGE), ex);
        }
    }

    private void sendBasicMailTemplate(String to, String subject, Map<String, Object> templateModel, MailTemplates templateEnum) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            Template template = getTemplate(templateEnum);
            String htmlContent = FreeMarkerTemplateUtils.processTemplateIntoString(template, templateModel);

            helper.setFrom(getFrom(mailFrom));
            helper.setTo(to);
            helper.setText(htmlContent, true);
            helper.addInline("logoId", new ClassPathResource("images/logo.png"));
            if (templateEnum.equals(MailTemplates.REGISTER_USER)) {
                helper.addInline("navButtonId", new ClassPathResource("images/navToItexButton.png"));
            }
            helper.setSubject(subject);
            sendAndRestoreMail(message);
        } catch (TemplateException | MessagingException | IOException ex) {
            throw new NotSendEmailException(simpleMessage(TEMPLATE_ERROR_MESSAGE), ex);
        }
    }

    private Template getTemplate(MailTemplates template) throws IOException {
        if (template.equals(MailTemplates.REGISTER_USER))
            return freemarkerConfig.getTemplate("register.ftl");
        if (template.equals(MailTemplates.CLIENT_NOTIFICATION))
            return freemarkerConfig.getTemplate("client-notification.ftl");
        if (template.equals(MailTemplates.COMMERCIAL))
            return freemarkerConfig.getTemplate("commercial.ftl");
        throw new NotSendEmailException(simpleMessage(TEMPLATE_ERROR_MESSAGE));
    }

    private void sendBasicAction(String to, String subject, String body) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(getFrom(mailFrom));
        email.setTo(to);
        email.setSubject(subject);
        email.setText(body);
        sendAndRestoreMail(email);
    }

    private String getFrom(String fromMail) {
        return "ITEX Software - ITS<" + fromMail + ">";
    }

    private void sendAndRestoreMail(SimpleMailMessage email) {
        emailSender.send(email);
        restoreMail();
    }

    private void sendAndRestoreMail(MimeMessage email) {
        emailSender.send(email);
        restoreMail();
    }

    private void restoreMail() {
        emailSender.setPassword(mailFromPass);
        emailSender.setUsername(mailFrom);
    }
}
