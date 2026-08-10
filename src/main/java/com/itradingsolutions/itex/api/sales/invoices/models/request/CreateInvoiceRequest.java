package com.itradingsolutions.itex.api.sales.invoices.models.request;

import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveDepartment;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.common.util.models.enums.Incoterms;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceVia;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Body of {@code POST /sales/invoice}. The ship to block is not received here: it is auto-loaded
 * from the Client and can be changed afterwards through the update endpoint. {@code salesRep},
 * {@code status} and {@code draftNumber} are assigned by the server.
 */
public record CreateInvoiceRequest(

        @NotNull(message = "Client is required")
        UUID clientId,

        // Optional: if omitted, the Client's main active contact (any department) is used.
        // The invoice always ends up with a contact — it is a required column.
        UUID clientContactId,

        @NotNull(message = "Incoterms is required")
        Incoterms incoterms,

        Currency currency,

        ConsecutiveDepartment department,

        InvoiceVia via,

        @Size(max = 100, message = "The order number cannot exceed 100 characters")
        String orderNumber,

        @Size(max = 100, message = "The AWB/BL cannot exceed 100 characters")
        String awbBl,

        String remarks,

        String internalRemarks,

        @Size(max = 100, message = "The packing list cannot exceed 100 characters")
        String packingList
) {}
