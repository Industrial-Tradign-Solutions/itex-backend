package com.itradingsolutions.itex.api.common.util.models.enums;

import lombok.Getter;

@Getter
public enum UnitType implements BaseEnum {
    GL("GL"),
    GR("GR"),
    HUNDRED("HUNDRED"),
    IN("IN"),
    OZ("OZ"),
    PAIL("PAIL"),
    PAIR("PAIR"),
    YD("YD"),
    THOUSAND("THOUSAND"),
    SQM("SQM"),
    BOX("BOX"),
    SHORT_TON("SHORT TON"),
    SET_ROLL("SET ROLL"),
    QUART("QUART"),
    PT("PT"),
    PKG("PKG"),
    PC("PC")
    ;
    private final String name;
    UnitType(final String name) {
        this.name = name;
    }
}
