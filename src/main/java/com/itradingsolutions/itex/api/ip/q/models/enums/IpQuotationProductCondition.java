package com.itradingsolutions.itex.api.ip.q.models.enums;

import com.itradingsolutions.itex.api.common.util.models.enums.BaseEnum;
import lombok.Getter;

@Getter
public enum IpQuotationProductCondition implements BaseEnum {
    NEW("NEW"),
    USED("USED");

    private final String name;

    IpQuotationProductCondition(final String name) {
        this.name = name;
    }
}
