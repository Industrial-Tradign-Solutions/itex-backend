package com.itradingsolutions.itex.api.sales.invoices.models.mapper;

import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoicePaymentDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoicePaymentEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.request.RegisterInvoicePaymentRequest;
import com.itradingsolutions.itex.api.sales.invoices.models.response.InvoicePaymentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InvoicePaymentMapper {

    InvoicePaymentDTO entityToDTO(InvoicePaymentEntity entity);

    /**
     * Maps only the caller-provided fields (amount, date, method, notes); the service completes the
     * server-side ones (invoice, receipt, registeredBy, createdAt).
     */
    InvoicePaymentEntity requestToEntity(RegisterInvoicePaymentRequest request);

    /** {@code receiptPath} is deliberately not exposed — it is a server-side filesystem location. */
    InvoicePaymentResponse dtoToResponse(InvoicePaymentDTO dto);
}
