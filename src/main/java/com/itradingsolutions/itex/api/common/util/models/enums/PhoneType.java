package com.itradingsolutions.itex.api.common.util.models.enums;

import lombok.Getter;

@Getter
public enum PhoneType implements BaseEnum{
    OFFICE1("OFFICE 1"),
    OFFICE2("OFFICE 2"),
    OFFICE3("OFFICE 3"),
    MOBILE1("MOBILE 1"),
    MOBILE2("MOBILE 2"),
    MOBILE3("MOBILE 3")


    ;

    private final String name;
    PhoneType(final String name) {
        this.name = name;
    }
}
