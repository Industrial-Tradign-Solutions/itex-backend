package com.itradingsolutions.itex.api.ip.q.models.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record IpQuotationOtherChargesBulkImportRequest(
        @NotEmpty List<@Valid IpQuotationOtherChargesImportItem> items
) {
}

