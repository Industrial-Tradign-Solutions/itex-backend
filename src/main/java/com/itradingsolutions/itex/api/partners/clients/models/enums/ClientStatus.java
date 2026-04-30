package com.itradingsolutions.itex.api.partners.clients.models.enums;

import com.itradingsolutions.itex.api.common.util.models.enums.BaseEnum;
import lombok.Getter;

@Getter
public enum ClientStatus implements BaseEnum {
    ACTIVE("ACTIVE"),
    PROSPECT("PROSPECT"),
    INACTIVE("INACTIVE");

    private final String name;
    ClientStatus(final String name) {
        this.name = name;
    }
}
