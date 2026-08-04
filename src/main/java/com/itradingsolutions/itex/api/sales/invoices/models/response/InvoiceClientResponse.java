package com.itradingsolutions.itex.api.sales.invoices.models.response;

import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;
import com.itradingsolutions.itex.api.masters.location.models.responses.BasicCityResponse;

import java.util.UUID;

/**
 * Bill-to block of an Invoice. Deliberately narrower than {@code ClientResponse}: it carries no
 * collections, so mapping a Client into an Invoice never walks
 * {@code infoByDepartment -> listContacts -> listPhones}.
 */
public record InvoiceClientResponse(
        UUID id,
        String code,
        String name,
        String showName,
        String taxId,
        String address,
        BasicCityResponse city,
        String zipCode,
        String phoneNumber,
        PaymentTerms paymentTerms
) {
}
