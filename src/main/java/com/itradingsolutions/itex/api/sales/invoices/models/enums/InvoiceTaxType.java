package com.itradingsolutions.itex.api.sales.invoices.models.enums;

import com.itradingsolutions.itex.api.common.util.models.enums.BaseEnum;
import lombok.Getter;

@Getter
public enum InvoiceTaxType implements BaseEnum {
    US_SALES_TAX("US SALES TAX"),
    COLOMBIA_IVA("COLOMBIA IVA"),
    WITHHOLDING_TAX("WITHHOLDING TAX"),
    VAT("VAT"),
    GST("GST"),
    EXPORT_TAX_EXEMPT("EXPORT TAX EXEMPT"),
    OTHER("OTHER");

    private final String name;

    InvoiceTaxType(final String name) {
        this.name = name;
    }
}
