package com.itradingsolutions.itex.config.security.auth;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleOption;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessToModule {
    ModuleOption option();
}

