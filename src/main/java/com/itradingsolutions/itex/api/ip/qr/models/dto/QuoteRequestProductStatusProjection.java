package com.itradingsolutions.itex.api.ip.qr.models.dto;

import com.itradingsolutions.itex.api.ip.products.models.enums.IpProductStatus;

/**
 * Proyección mínima de un producto en un quote request para reportar productos que
 * no cumplen un estatus esperado.
 */
public record QuoteRequestProductStatusProjection(String mfrReference, String description, IpProductStatus status) {
}
