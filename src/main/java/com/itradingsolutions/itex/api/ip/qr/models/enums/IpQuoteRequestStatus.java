package com.itradingsolutions.itex.api.ip.qr.models.enums;

import com.itradingsolutions.itex.api.common.util.models.enums.BaseEnum;
import lombok.Getter;

@Getter
public enum IpQuoteRequestStatus implements BaseEnum {
    CREATED("CREATED"),
    SENT("SENT"),
    ANSWERED("ANSWERED"),
    COMPLETE("COMPLETE"),
    REJECTED("REJECTED"),
    ;
    private final String name;
    IpQuoteRequestStatus(final String name) {
        this.name = name;
    }
}
