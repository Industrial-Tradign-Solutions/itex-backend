package com.itradingsolutions.itex.api.ip.products.models.enums;

import com.itradingsolutions.itex.api.common.util.models.enums.BaseEnum;
import lombok.Getter;

@Getter
public enum ProductCondition implements BaseEnum {
    NEW("NEW"),
    USED("USED");

    private final String name;

    ProductCondition(final String name) {
        this.name = name;
    }
}
