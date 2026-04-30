package com.itradingsolutions.itex.api.common.util.models.enums;

import lombok.Getter;

@Getter
public enum Language implements BaseEnum {
    ENGLISH("ENGLISH"),
    SPANISH("SPANISH");

    private final String name;
    Language(final String name) {
        this.name = name;
    }
}
