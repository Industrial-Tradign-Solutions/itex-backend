package com.itradingsolutions.itex.api.ip.po.models.enums;

import com.itradingsolutions.itex.api.common.util.models.enums.BaseEnum;
import lombok.Getter;

@Getter
public enum PurchaseOrderStatus implements BaseEnum {
    CREATED("CREATED"),
    SENT("SENT"),
    ANSWERED("ANSWERED"),
    COMPLETE("COMPLETE"),
    REJECTED("REJECTED");

    private final String name;

    PurchaseOrderStatus(final String name) {
        this.name = name;
    }
}
