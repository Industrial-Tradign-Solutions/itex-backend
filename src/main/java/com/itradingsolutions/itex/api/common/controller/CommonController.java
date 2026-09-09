package com.itradingsolutions.itex.api.common.controller;

import com.itradingsolutions.itex.api.admin.user.models.dto.UserDTO;
import com.itradingsolutions.itex.api.common.models.enums.OpenAndLockType;
import com.itradingsolutions.itex.api.common.service.IMessageService;
import com.itradingsolutions.itex.api.common.util.services.IHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class CommonController {

    protected static final String SUCCESS_TITLE = "Success";

    @Autowired
    protected IHistoryService historyService;

    @Autowired
    private IMessageService messageService;

    protected String getUserAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    protected boolean isOpenByUsername(UserDTO openUser, OpenAndLockType type) {
        if (type.equals(OpenAndLockType.VIEW)) return true;
        return openUser.getUser().equals(getUserAuthenticated());
    }

    protected String simpleMessage(String template) {
        return messageService.simpleMessage(template);
    }

    protected String compositeMessage(String template, String[] attributes) {
        return messageService.compositeMessage(template, attributes);
    }
}
