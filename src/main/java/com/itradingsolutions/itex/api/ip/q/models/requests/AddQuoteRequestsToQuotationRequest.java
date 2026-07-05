package com.itradingsolutions.itex.api.ip.q.models.requests;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

/**
 * Request to add Quote Requests to an existing Quotation.
 */
public record AddQuoteRequestsToQuotationRequest(
        @NotEmpty(message = "Quote Request IDs list cannot be empty")
        List<UUID> quoteRequestIds
) {
}
