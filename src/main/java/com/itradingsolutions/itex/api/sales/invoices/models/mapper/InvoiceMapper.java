package com.itradingsolutions.itex.api.sales.invoices.models.mapper;

import com.itradingsolutions.itex.api.ip.po.models.response.BasicIpPurchaseOrderResponse;
import com.itradingsolutions.itex.api.masters.location.models.dto.CityDTO;
import com.itradingsolutions.itex.api.masters.location.models.responses.BasicCityResponse;
import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceChargeDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoicePurchaseOrderDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceProductDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceTaxDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.request.CreateInvoiceRequest;
import com.itradingsolutions.itex.api.sales.invoices.models.request.UpdateInvoiceRequest;
import com.itradingsolutions.itex.api.sales.invoices.models.response.InvoiceBasicResponse;
import com.itradingsolutions.itex.api.sales.invoices.models.response.InvoiceChargeResponse;
import com.itradingsolutions.itex.api.sales.invoices.models.response.InvoiceClientResponse;
import com.itradingsolutions.itex.api.sales.invoices.models.response.InvoiceProductResponse;
import com.itradingsolutions.itex.api.sales.invoices.models.response.InvoiceResponse;
import com.itradingsolutions.itex.api.sales.invoices.models.response.InvoiceTaxResponse;
import com.itradingsolutions.itex.api.sales.invoices.models.response.ListInvoiceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.itradingsolutions.itex.api.sales.invoices.models.InvoiceNumberFormatter;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InvoiceMapper {


    // Kept deliberately light: client.infoByDepartment is ignored so mapping an invoice never
    // walks infoByDepartment -> listContacts -> listPhones, and the four child lists are ignored
    // because listAll/listAllOpenByUser/the history diff's oldDto don't need them — only
    // InvoiceDetailResolver fills them, for the three endpoints that actually return a detail.
    @Mapping(target = "client.infoByDepartment", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "charges", ignore = true)
    @Mapping(target = "taxes", ignore = true)
    @Mapping(target = "linkedPurchaseOrders", ignore = true)
    InvoiceDTO entityToDTO(InvoiceEntity entity);

    InvoiceDTO createRequestToDTO(CreateInvoiceRequest request);

    InvoiceDTO updateRequestToDTO(UpdateInvoiceRequest request);

    @Mapping(target = "draftNumber", source = "draftNumber", qualifiedByName = "formatNumber")
    @Mapping(target = "number", source = "number", qualifiedByName = "formatNumber")
    @Mapping(target = "name", expression = "java(dto.getName())")
    @Mapping(target = "balanceDue", expression = "java(dto.getTotalAmount().subtract(dto.getPaidAmount()))")
    @Mapping(target = "client", expression = "java(toClientResponse(dto))")
    @Mapping(target = "products", expression = "java(mapProductsResponse(dto))")
    @Mapping(target = "charges", expression = "java(mapChargesResponse(dto))")
    @Mapping(target = "taxes", expression = "java(mapTaxesResponse(dto))")
    @Mapping(target = "linkedPurchaseOrders", expression = "java(mapLinkedPOsResponse(dto))")
    @Mapping(target = "clonedInvoices", expression = "java(mapClonedInvoicesResponse(dto))")
    @Mapping(target = "clonedByInvoice", expression = "java(mapClonedByInvoiceResponse(dto))")
    @Mapping(target = "productsTotal", expression = "java(dto.getProductsTotal())")
    @Mapping(target = "chargesTotal", expression = "java(dto.getChargesTotal())")
    @Mapping(target = "taxesTotal", expression = "java(dto.getTaxesTotal())")
    @Mapping(target = "paidLate", expression = "java(dto.isPaidLate())")
    InvoiceResponse dtoToResponse(InvoiceDTO dto);

    @Mapping(target = "draftNumber", source = "draftNumber", qualifiedByName = "formatNumber")
    @Mapping(target = "number", source = "number", qualifiedByName = "formatNumber")
    @Mapping(target = "name", expression = "java(dto.getName())")
    @Mapping(target = "balanceDue", expression = "java(dto.getTotalAmount().subtract(dto.getPaidAmount()))")
    @Mapping(target = "client", expression = "java(toClientResponse(dto))")
    @Mapping(target = "paidLate", expression = "java(dto.isPaidLate())")
    ListInvoiceResponse dtoToListResponse(InvoiceDTO dto);

    default InvoiceClientResponse toClientResponse(InvoiceDTO dto) {
        ClientDTO client = dto.getClient();
        if (client == null) return null;
        return new InvoiceClientResponse(
                client.getId(), client.getCode(), client.getName(), client.getShowName(),
                client.getTaxId(), client.getAddress(), toCityResponse(client.getCity()),
                client.getZipCode(), client.getPhoneNumber(), client.getPaymentTerms());
    }

    BasicCityResponse toCityResponse(CityDTO city);

    @Mapping(target = "ipProduct", source = "product")
    @Mapping(target = "extendedPrice", expression = "java(dto.getExtendedPrice())")
    @Mapping(target = "sellingUnitPrice", expression = "java(dto.getSellingUnitPrice())")
    @Mapping(target = "sellingExtendedPrice", expression = "java(dto.getSellingExtendedPrice())")
    InvoiceProductResponse toProductResponse(InvoiceProductDTO dto);

    InvoiceChargeResponse toChargeResponse(InvoiceChargeDTO dto);

    InvoiceTaxResponse toTaxResponse(InvoiceTaxDTO dto);

    default List<InvoiceProductResponse> mapProductsResponse(InvoiceDTO dto) {
        if (dto.getProducts() == null) return Collections.emptyList();
        return dto.getProducts().stream().map(this::toProductResponse).toList();
    }

    default List<InvoiceChargeResponse> mapChargesResponse(InvoiceDTO dto) {
        if (dto.getCharges() == null) return Collections.emptyList();
        return dto.getCharges().stream().map(this::toChargeResponse).toList();
    }

    default List<InvoiceTaxResponse> mapTaxesResponse(InvoiceDTO dto) {
        if (dto.getTaxes() == null) return Collections.emptyList();
        return dto.getTaxes().stream().map(this::toTaxResponse).toList();
    }

    default List<BasicIpPurchaseOrderResponse> mapLinkedPOsResponse(InvoiceDTO dto) {
        if (dto.getLinkedPurchaseOrders() == null) return Collections.emptyList();
        return dto.getLinkedPurchaseOrders().stream()
                .map(this::toLinkedPoResponse)
                .toList();
    }

    default BasicIpPurchaseOrderResponse toLinkedPoResponse(InvoicePurchaseOrderDTO po) {
        return new BasicIpPurchaseOrderResponse(po.getId(), po.getNumber());
    }

    default List<InvoiceBasicResponse> mapClonedInvoicesResponse(InvoiceDTO dto) {
        if (dto.getClonedInvoices() == null) return Collections.emptyList();
        return dto.getClonedInvoices().stream()
                .map(this::toBasicInvoiceResponse)
                .toList();
    }

    default InvoiceBasicResponse mapClonedByInvoiceResponse(InvoiceDTO dto) {
        if (dto.getClonedByInvoice() == null) return null;
        return toBasicInvoiceResponse(dto.getClonedByInvoice());
    }

    default InvoiceBasicResponse toBasicInvoiceResponse(InvoiceDTO invoice) {
        if (invoice == null) return null;
        String number = formatNumber(invoice.getNumber() != null ? invoice.getNumber() : invoice.getDraftNumber());
        return new InvoiceBasicResponse(invoice.getId(), number);
    }

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
        return InvoiceNumberFormatter.format(value);
    }
}
