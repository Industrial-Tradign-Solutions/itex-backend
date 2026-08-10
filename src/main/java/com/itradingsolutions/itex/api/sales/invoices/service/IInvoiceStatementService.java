package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.sales.invoices.models.response.ClientStatementResponse;

import java.util.UUID;

public interface IInvoiceStatementService {

    /**
     * Account statement of a Client, restricted to the invoices the caller is allowed to see:
     * without {@code VIEW_ALL_INVOICE} the figures only cover their own (invoicing guide §12).
     */
    ClientStatementResponse statementByClient(UUID clientId);
}
