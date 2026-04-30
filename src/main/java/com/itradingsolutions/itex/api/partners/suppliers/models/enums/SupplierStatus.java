package com.itradingsolutions.itex.api.partners.suppliers.models.enums;

import com.itradingsolutions.itex.api.common.util.models.enums.BaseEnum;
import lombok.Getter;

@Getter
public enum SupplierStatus implements BaseEnum {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");

    private final String name;
    SupplierStatus(final String name) {
        this.name = name;
    }
}
