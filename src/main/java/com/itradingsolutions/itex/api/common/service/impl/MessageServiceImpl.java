package com.itradingsolutions.itex.api.common.service.impl;

import com.itradingsolutions.itex.api.common.service.IMessageService;
import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class MessageServiceImpl implements IMessageService {

    @Autowired
    @Qualifier("messageSource")
    private MessageSource ms;

    @Override
    public String simpleMessage(String template) {
        String message;
        try{
            message = ms.getMessage(template, null, Locale.getDefault());
        }catch (NoSuchMessageException e){
            throw new NotFoundException("No Message Found", e);
        }
        return message;
    }

    @Override
    public String compositeMessage(String template, String[] attributes) {
        String message;
        try{
            message = ms.getMessage(template, attributes, Locale.getDefault());
        }catch (NoSuchMessageException e){
            throw new NotFoundException("No Message Found", e);
        }
        return message;
    }
}
