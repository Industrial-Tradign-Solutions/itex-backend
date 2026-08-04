package com.itradingsolutions.itex.api.sales.invoices.models.mapper;

import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceTaxDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceTaxEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.request.InvoiceTaxRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InvoiceTaxMapper {

    InvoiceTaxDTO entityToDTO(InvoiceTaxEntity entity);

    InvoiceTaxDTO requestToDTO(InvoiceTaxRequest request);
}
