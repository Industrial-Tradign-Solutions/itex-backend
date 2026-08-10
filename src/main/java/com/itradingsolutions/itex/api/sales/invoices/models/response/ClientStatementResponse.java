package com.itradingsolutions.itex.api.sales.invoices.models.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Account statement of a Client: how much has been billed, how much is still owed, how that debt
 * ages, and which invoices are currently overdue. Aggregated over the existing schema — cancelled
 * invoices are excluded, since they represent nothing owed.
 */
public record ClientStatementResponse(
        UUID clientId,
        String clientName,
        int invoiceCount,
        BigDecimal totalInvoiced,
        BigDecimal totalPaid,
        BigDecimal totalOutstanding,
        AgingBucketResponse aging,
        List<ListInvoiceResponse> overdueInvoices
) {
}
