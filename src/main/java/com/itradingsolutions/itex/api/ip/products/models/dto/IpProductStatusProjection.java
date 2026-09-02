package com.itradingsolutions.itex.api.ip.products.models.dto;

import com.itradingsolutions.itex.api.ip.products.models.enums.IpProductStatus;

import java.util.UUID;

/**
 * Proyección mínima de un producto para consultar su estatus sin cargar la entidad completa.
 */
public record IpProductStatusProjection(UUID id, IpProductStatus status) {
}
