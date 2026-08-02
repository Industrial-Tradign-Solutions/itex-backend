package com.itradingsolutions.itex.api.sales.invoices.models.enums;

import com.itradingsolutions.itex.api.common.util.models.enums.BaseEnum;
import lombok.Getter;

@Getter
public enum InvoiceVia implements BaseEnum {
    BL("BL"),
    AWB("AWB");

    private final String name;

    InvoiceVia(final String name) {
        this.name = name;
    }
}
