package com.itradingsolutions.itex.api.common.util.models.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod implements BaseEnum{

    ACH("ACH"),
    CREDIT_CARD("CREDIT CARD"),
    WIRE_TRANSFER("WIRE TRANSFER"),
    CHECK("CHECK"),


    ;

    private final String name;
    PaymentMethod(final String name) {
        this.name = name;
    }
}
