package com.itradingsolutions.itex.api.sales.invoices.models.mapper;

import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceHistoryDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceHistoryEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.response.InvoiceHistoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InvoiceHistoryMapper {

    InvoiceHistoryDTO entityToDTO(InvoiceHistoryEntity entity);

    InvoiceHistoryResponse dtoToResponse(InvoiceHistoryDTO dto);
}
