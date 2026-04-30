package com.itradingsolutions.itex.api.common.models.enums;

import com.itradingsolutions.itex.api.common.util.models.enums.BaseEnum;
import lombok.Getter;

@Getter
public enum LeadTime implements BaseEnum {
    WEEKS("WEEKS"),
    MONTHS("MONTHS"),
    DAYS("DAYS"),
    ;
    private final String name;
    LeadTime(final String name) {
        this.name = name;
    }
}
