package com.itradingsolutions.itex.api.sales.invoices.models.enums;

import com.itradingsolutions.itex.api.common.util.models.enums.BaseEnum;
import lombok.Getter;

import java.util.Set;

@Getter
public enum InvoiceStatus implements BaseEnum {
    DRAFT("DRAFT"),
    ISSUED("ISSUED"),
    PARTIAL_PAID("PARTIAL PAID"),
    PAID("PAID"),
    CANCELLED("CANCELLED");

    /**
     * Issued with money still outstanding: the only statuses that can fall overdue and the only
     * ones that accept a payment.
     */
    public static final Set<InvoiceStatus> COLLECTABLE = Set.of(ISSUED, PARTIAL_PAID);

    /** Everything that was actually issued — what a client statement counts (guide §11). */
    public static final Set<InvoiceStatus> BILLED = Set.of(ISSUED, PARTIAL_PAID, PAID);

    /**
     * Statuses that still accept changes and are therefore worth locking: DRAFT is editable,
     * ISSUED/PARTIAL_PAID accept payment registration, and PAID accepts voiding a payment — the
     * correction path of guide §7.6. CANCELLED is final; locking it would only orphan locks.
     */
    public static final Set<InvoiceStatus> LOCKABLE = Set.of(DRAFT, ISSUED, PARTIAL_PAID, PAID);

    private final String name;

    InvoiceStatus(final String name) {
        this.name = name;
    }
}
