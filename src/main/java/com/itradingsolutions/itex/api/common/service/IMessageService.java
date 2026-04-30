package com.itradingsolutions.itex.api.common.service;

public interface IMessageService {
    String simpleMessage(String template);
    String compositeMessage(String template, String[] attributes);
}
