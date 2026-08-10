package com.itradingsolutions.itex.api.common.util.models.enums;

/**
 * Formula family a {@link PaymentTerms} value follows when a document computes its due date.
 * Internal to the calculation — deliberately not registered in {@code GET /common/static_lists}.
 */
public enum DueDateRule {

    /** Due date is the issue date plus a fixed number of days. */
    NET_DAYS,

    /** Due date is a fixed day of the month following the issue date. */
    PROX_DAY,

    /** Due date is the last day of the issue month plus a fixed number of days. */
    END_OF_MONTH_PLUS_DAYS,

    /** Due date is the issue date itself. */
    ON_ISSUE,

    /** The term does not express a period after issuance, so no due date can be derived. */
    NOT_CALCULABLE
}
