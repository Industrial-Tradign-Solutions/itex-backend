package com.itradingsolutions.itex.api.ip.products.models.enums;

import com.itradingsolutions.itex.api.common.util.models.enums.BaseEnum;
import lombok.Getter;

@Getter
public enum IpProductStatus implements BaseEnum {
    ACTIVE("ACTIVE"),
    SUBSTITUTED("SUBSTITUTED"),
    INACTIVE("INACTIVE")
    ;

    private final String name;
    IpProductStatus(final String name) {
        this.name = name;
    }
}
