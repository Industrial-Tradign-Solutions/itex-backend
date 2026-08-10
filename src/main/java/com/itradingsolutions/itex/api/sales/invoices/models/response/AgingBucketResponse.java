package com.itradingsolutions.itex.api.sales.invoices.models.response;

import java.math.BigDecimal;

/**
 * Outstanding balance split by how long it has been overdue — the classic accounts-receivable
 * aging report (invoicing guide §11). {@code current} is what is not due yet; the rest are days
 * elapsed since {@code due_at}.
 */
public record AgingBucketResponse(
        BigDecimal current,
        BigDecimal days1To30,
        BigDecimal days31To60,
        BigDecimal days61To90,
        BigDecimal days90Plus
) {
}
