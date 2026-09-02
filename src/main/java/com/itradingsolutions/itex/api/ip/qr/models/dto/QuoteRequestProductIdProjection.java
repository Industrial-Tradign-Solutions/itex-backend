package com.itradingsolutions.itex.api.ip.qr.models.dto;

import java.util.UUID;

/**
 * Proyección mínima que mapea un quote request product con el id de su producto IP.
 */
public record QuoteRequestProductIdProjection(UUID id, UUID productId) {
}
