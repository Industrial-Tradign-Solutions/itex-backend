package com.itradingsolutions.itex.api.common.util.models.enums;

import lombok.Getter;

/**
 * Payment terms of a document. Besides the display name shown in dropdowns, each value carries the
 * {@link DueDateRule} it follows and the numeric operand that rule needs (days for {@code NET_DAYS}
 * and {@code END_OF_MONTH_PLUS_DAYS}, day of month for {@code PROX_DAY}, {@code 0} when unused), so
 * a due date can be derived without parsing the identifier.
 */
@Getter
public enum PaymentTerms implements BaseEnum{
    NET_5("NET 5", DueDateRule.NET_DAYS, 5),
    NET_7("NET 7", DueDateRule.NET_DAYS, 7),
    NET_8("NET 8", DueDateRule.NET_DAYS, 8),
    NET_10("NET 10", DueDateRule.NET_DAYS, 10),
    NET_14("NET 14", DueDateRule.NET_DAYS, 14),
    NET_15("NET 15", DueDateRule.NET_DAYS, 15),
    NET_20("NET 20", DueDateRule.NET_DAYS, 20),
    NET_21("NET 21", DueDateRule.NET_DAYS, 21),
    NET_30("NET 30", DueDateRule.NET_DAYS, 30),
    NET_35("NET 35", DueDateRule.NET_DAYS, 35),
    NET_40("NET 40", DueDateRule.NET_DAYS, 40),
    NET_45("NET 45", DueDateRule.NET_DAYS, 45),
    NET_55("NET 55", DueDateRule.NET_DAYS, 55),
    NET_60("NET 60", DueDateRule.NET_DAYS, 60),
    NET_75("NET 75", DueDateRule.NET_DAYS, 75),
    NET_90("NET 90", DueDateRule.NET_DAYS, 90),
    NET_120("NET 120", DueDateRule.NET_DAYS, 120),
    NET_150("NET 150", DueDateRule.NET_DAYS, 150),
    NET_180("NET 180", DueDateRule.NET_DAYS, 180),
    NET_15TH_PROX("NET 15TH PROX", DueDateRule.PROX_DAY, 15),
    NET_20TH_PROX("NET 20TH PROX", DueDateRule.PROX_DAY, 20),
    NET_30TH_PROX("NET 30TH PROX", DueDateRule.PROX_DAY, 30),
    NET_30_END_OF_THE_MONTH("NET 30, END OF THE MONTH", DueDateRule.END_OF_MONTH_PLUS_DAYS, 30),
    NET_60_END_OF_THE_MONTH("NET 60, END OF THE MONTH", DueDateRule.END_OF_MONTH_PLUS_DAYS, 60),
    ADVANCED("ADVANCED", DueDateRule.NOT_CALCULABLE, 0),
    COD("COD", DueDateRule.ON_ISSUE, 0),
    DUE_UPON_RECEIPT("DUE UPON RECEIPT", DueDateRule.ON_ISSUE, 0),
    PRIOR_TO_SHIPMENT("PRIOR TO SHIPMENT", DueDateRule.NOT_CALCULABLE, 0),
    TO_BE_AGREED("TO BE AGREED", DueDateRule.NOT_CALCULABLE, 0),
    W_DOCUMENTS("W/DOCUMENTS", DueDateRule.NOT_CALCULABLE, 0);


    private final String name;
    private final DueDateRule rule;
    private final int value;

    PaymentTerms(final String name, final DueDateRule rule, final int value) {
        this.name = name;
        this.rule = rule;
        this.value = value;
    }

    /** Whether a due date can be derived from the issue date alone. */
    public boolean isCalculable() {
        return rule != DueDateRule.NOT_CALCULABLE;
    }
}
