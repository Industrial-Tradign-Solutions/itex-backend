package com.itradingsolutions.itex.api.ip.q.models.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record IpQuotationProductBulkRequest(
        @NotEmpty(message = "At least one product is required")
        List<@Valid IpQuotationProductRequest> products
) {}
