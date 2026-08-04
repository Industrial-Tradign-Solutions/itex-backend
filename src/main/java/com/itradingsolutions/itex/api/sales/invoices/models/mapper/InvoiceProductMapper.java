package com.itradingsolutions.itex.api.sales.invoices.models.mapper;

import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceProductDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceIpProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InvoiceProductMapper {

    // None of these belong on an invoice line: surplus/substituteProduct/openBy would otherwise
    // pull extra lazy relations (and substituteProduct is self-referencing) for no reason here.
    @Mapping(target = "product.surplus", ignore = true)
    @Mapping(target = "product.substituteProduct", ignore = true)
    @Mapping(target = "product.openBy", ignore = true)
    @Mapping(target = "product.openAt", ignore = true)
    InvoiceProductDTO entityToDTO(InvoiceIpProductEntity entity);
}
