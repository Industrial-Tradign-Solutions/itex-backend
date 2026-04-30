package com.itradingsolutions.itex.api.ip.q.models.response;

import com.itradingsolutions.itex.api.common.models.enums.OpenAndLockType;

public record CreateQuotationResponse(
        ListIpQuotationResponse item,
        OpenAndLockType type,
        Boolean pristine
) {
}
