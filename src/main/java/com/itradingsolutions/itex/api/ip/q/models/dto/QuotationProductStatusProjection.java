package com.itradingsolutions.itex.api.ip.q.models.dto;

import com.itradingsolutions.itex.api.ip.products.models.enums.IpProductStatus;

/**
 * Minimal projection of a product in a quotation to report products that do not
 * meet an expected status.
 */
public record QuotationProductStatusProjection(String mfrReference, String description, IpProductStatus status) {
}
