package com.itradingsolutions.itex.api.sales.invoices.service;

import com.itradingsolutions.itex.api.masters.location.models.entities.CityEntity;

/**
 * Ship to block copied from the Client at creation time. Denormalized on purpose: the invoice must
 * stay immutable even if the Client changes later.
 */
public record InvoiceShipToSnapshot(
        String name,
        String address,
        CityEntity city,
        String phone,
        String contactName,
        String email
) {}
