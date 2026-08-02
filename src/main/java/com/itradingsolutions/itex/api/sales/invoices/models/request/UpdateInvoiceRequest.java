package com.itradingsolutions.itex.api.sales.invoices.models.request;

import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.common.util.models.enums.Incoterms;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceVia;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Body of {@code PUT /sales/invoice/{invoice_id}}. Only applies to invoices in
 * {@code DRAFT}. {@code department} is not received: it is fixed at creation time.
 * {@code paymentTerms} and {@code salesRepId} are permission gated in the service.
 */
public record UpdateInvoiceRequest(

        @NotNull(message = "Client is required")
        UUID clientId,

        UUID clientContactId,

        @NotNull(message = "Incoterms is required")
        Incoterms incoterms,

        @NotNull(message = "Currency is required")
        Currency currency,

        InvoiceVia via,

        PaymentTerms paymentTerms,

        UUID salesRepId,

        @NotBlank(message = "Ship to name is required")
        @Size(max = 300, message = "The ship to name cannot exceed 300 characters")
        String shipToName,

        @NotBlank(message = "Ship to address is required")
        @Size(max = 500, message = "The ship to address cannot exceed 500 characters")
        String shipToAddress,

        @NotNull(message = "Ship to city is required")
        UUID shipToCityId,

        @NotBlank(message = "Ship to phone is required")
        @Size(max = 50, message = "The ship to phone cannot exceed 50 characters")
        String shipToPhone,

        @NotBlank(message = "Ship to contact name is required")
        @Size(max = 255, message = "The ship to contact name cannot exceed 255 characters")
        String shipToContactName,

        @NotBlank(message = "Ship to email is required")
        @Email(message = "The ship to email is not valid")
        @Size(max = 150, message = "The ship to email cannot exceed 150 characters")
        String shipToEmail,

        @Size(max = 100, message = "The order number cannot exceed 100 characters")
        String orderNumber,

        @Size(max = 100, message = "The AWB/BL cannot exceed 100 characters")
        String awbBl,

        String remarks,

        String internalRemarks,

        @Size(max = 100, message = "The packing list cannot exceed 100 characters")
        String packingList
) {}
