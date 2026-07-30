package com.itradingsolutions.itex.api.sales.invoices.models.enums;

import com.itradingsolutions.itex.api.common.util.models.enums.BaseEnum;
import lombok.Getter;

@Getter
public enum InvoiceChargeType implements BaseEnum {
    INTERNATIONAL_FREIGHT("INTERNATIONAL FREIGHT"),
    LOCAL_FREIGHT("LOCAL FREIGHT"),
    INSURANCE("INSURANCE"),
    WIRE_TRANSFER_FEE("WIRE TRANSFER FEE"),
    CUSTOMS_DUTIES("CUSTOMS DUTIES"),
    CUSTOMS_BROKERAGE_FEE("CUSTOMS BROKERAGE FEE"),
    HANDLING_FEE("HANDLING FEE"),
    PACKING_FEE("PACKING FEE"),
    STORAGE_FEE("STORAGE FEE"),
    INSPECTION_FEE("INSPECTION FEE"),
    BANK_FEE("BANK FEE"),
    DISCOUNT("DISCOUNT"),
    OTHER("OTHER");

    private final String name;

    InvoiceChargeType(final String name) {
        this.name = name;
    }
}
