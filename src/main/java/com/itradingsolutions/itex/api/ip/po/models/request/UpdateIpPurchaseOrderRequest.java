package com.itradingsolutions.itex.api.ip.po.models.request;

import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateIpPurchaseOrderRequest(

        UUID clientId,

        UUID clientContactId,

        @Size(max = 50, message = "The client PO number cannot exceed 50 characters")
        String clientPoNumber,

        Currency currency,

        UUID supplierId,

        UUID supplierContactId,

        @Size(max = 50, message = "The supplier PO number cannot exceed 50 characters")
        String supplierPoNumber,

        PaymentTerms paymentTerms,

        @Size(max = 50, message = "The shipping method cannot exceed 50 characters")
        String shippingMethod,

        String remarks,

        String internalRemarks,

        UUID salesRepId,

        @NotNull(message = "Lead time is required")
        @Min(value = 0, message = "The lead time cannot be negative")
        Integer leadTime,

        @NotNull(message = "Lead time type is required")
        LeadTime leadTimeType,

        @NotNull(message = "Sales tax is required")
        @DecimalMin(value = "0", message = "The sales tax cannot be negative")
        BigDecimal salesTax,

        @NotBlank(message = "Ship to name is required")
        @Size(max = 300, message = "The ship to name cannot exceed 300 characters")
        String shipToName,

        @NotBlank(message = "Ship to address is required")
        @Size(max = 500, message = "The ship to address cannot exceed 500 characters")
        String shipToAddress,

        @NotNull(message = "Ship to city is required")
        UUID shipToCityId,

        @NotBlank(message = "Ship to phone is required")
        @Size(max = 20, message = "The ship to phone cannot exceed 20 characters")
        String shipToPhone,

        @NotBlank(message = "Ship to contact name is required")
        @Size(max = 50, message = "The ship to contact name cannot exceed 50 characters")
        String shipToContactName,

        @NotBlank(message = "Ship to email is required")
        @Email(message = "The ship to email is not valid")
        @Size(max = 100, message = "The ship to email cannot exceed 100 characters")
        String shipToEmail
) {
}
