package com.itradingsolutions.itex.api.sales.invoices.models.enums;

import com.itradingsolutions.itex.api.common.util.models.enums.BaseEnum;
import lombok.Getter;

@Getter
public enum InvoiceStatus implements BaseEnum {
    DRAFT("DRAFT"),
    ISSUED("ISSUED"),
    PARTIAL_PAID("PARTIAL PAID"),
    PAID("PAID"),
    CANCELLED("CANCELLED");

    private final String name;

    InvoiceStatus(final String name) {
        this.name = name;
    }
}
