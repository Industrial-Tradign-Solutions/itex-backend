package com.itradingsolutions.itex.api.sales.invoices.models.mapper;

import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.response.InvoiceResponse;
import com.itradingsolutions.itex.api.sales.invoices.models.response.ListInvoiceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InvoiceMapper {

    int NUMBER_PAD = 6;

    InvoiceDTO entityToDTO(InvoiceEntity entity);

    @Mapping(target = "draftNumber", source = "draftNumber", qualifiedByName = "formatNumber")
    @Mapping(target = "number", source = "number", qualifiedByName = "formatNumber")
    @Mapping(target = "name", expression = "java(dto.getName())")
    @Mapping(target = "balanceDue", expression = "java(dto.getTotalAmount().subtract(dto.getPaidAmount()))")
    InvoiceResponse dtoToResponse(InvoiceDTO dto);

    @Mapping(target = "draftNumber", source = "draftNumber", qualifiedByName = "formatNumber")
    @Mapping(target = "number", source = "number", qualifiedByName = "formatNumber")
    @Mapping(target = "name", expression = "java(dto.getName())")
    @Mapping(target = "balanceDue", expression = "java(dto.getTotalAmount().subtract(dto.getPaidAmount()))")
    ListInvoiceResponse dtoToListResponse(InvoiceDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "draftNumber", ignore = true)
    @Mapping(target = "number", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "salesRep", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "paidAmount", ignore = true)
    @Mapping(target = "dueAt", ignore = true)
    @Mapping(target = "overdue", ignore = true)
    @Mapping(target = "overdueNotifiedAt", ignore = true)
    @Mapping(target = "issuedAt", ignore = true)
    @Mapping(target = "partialPaidAt", ignore = true)
    @Mapping(target = "paidAt", ignore = true)
    @Mapping(target = "cancelledAt", ignore = true)
    @Mapping(target = "cancelReason", ignore = true)
    @Mapping(target = "pdfUrl", ignore = true)
    @Mapping(target = "openAt", ignore = true)
    @Mapping(target = "openBy", ignore = true)
    @Mapping(target = "charges", ignore = true)
    @Mapping(target = "taxes", ignore = true)
    @Mapping(target = "payments", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "linkedPurchaseOrders", ignore = true)
    @Mapping(target = "clonedInvoices", ignore = true)
    InvoiceEntity clone(InvoiceEntity entity);

    @org.mapstruct.Named("formatNumber")
    static String formatNumber(Long value) {
        if (value == null) return null;
        return String.format("%0" + NUMBER_PAD + "d", value);
    }
}
