package com.itradingsolutions.itex.api.sales.invoices.models.mapper;

import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceChargeDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceChargeEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.request.InvoiceChargeRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InvoiceChargeMapper {

    InvoiceChargeDTO entityToDTO(InvoiceChargeEntity entity);

    InvoiceChargeDTO requestToDTO(InvoiceChargeRequest request);
}
