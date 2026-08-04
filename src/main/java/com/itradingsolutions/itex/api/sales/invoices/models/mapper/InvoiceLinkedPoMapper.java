package com.itradingsolutions.itex.api.sales.invoices.models.mapper;

import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoicePurchaseOrderDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceIpPoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InvoiceLinkedPoMapper {

    @Mapping(target = "id", source = "purchaseOrder.id")
    @Mapping(target = "number", source = "purchaseOrder.number")
    InvoicePurchaseOrderDTO entityToDTO(InvoiceIpPoEntity entity);
}
