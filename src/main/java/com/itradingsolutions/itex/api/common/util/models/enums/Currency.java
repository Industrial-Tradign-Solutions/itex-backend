package com.itradingsolutions.itex.api.common.util.models.enums;

import lombok.Getter;

@Getter
public enum Currency implements BaseEnum {
    USD("USD"),
    EUR("EUR"),
    JPY("JPY"),
    GBP("GBP")
    ;
    private final String name;
    Currency(final String name) {
        this.name = name;
    }
}
