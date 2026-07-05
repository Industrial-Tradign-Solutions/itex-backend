package com.itradingsolutions.itex.api.ip.q.models.enums;

import com.itradingsolutions.itex.api.common.util.models.enums.BaseEnum;
import lombok.Getter;

@Getter
public enum IpQuotationStatus implements BaseEnum {
    CREATED("CREATED"),
    SENT("SENT"),
    ANSWERED("ANSWERED"),
    COMPLETE("COMPLETE"),
    REJECTED("REJECTED"),
            ;
    private final String name;
    IpQuotationStatus(final String name) {
        this.name = name;
    }
}
