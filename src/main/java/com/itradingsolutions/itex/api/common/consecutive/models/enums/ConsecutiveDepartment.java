package com.itradingsolutions.itex.api.common.consecutive.models.enums;

import com.itradingsolutions.itex.api.common.util.models.enums.BaseEnum;
import lombok.Getter;

@Getter
public enum ConsecutiveDepartment implements BaseEnum {
    IP("Industrial Purchases"),
    RM("Raw Materials"),
    IF("Inland Freight"),
    LO("Logistics Operations"),
    ACC("Accounting");

    private final String name;

    ConsecutiveDepartment(final String name) {
        this.name = name;
    }
}
