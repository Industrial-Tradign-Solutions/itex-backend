package com.itradingsolutions.itex.api.sales.invoices.models.dto.reports;

import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.InvoiceMoney;
import com.itradingsolutions.itex.api.sales.invoices.models.InvoiceNumberFormatter;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceStatus;
import lombok.Getter;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Flat, pre-formatted view of an Invoice for Jasper — every field is already a {@code String}, which
 * is where the guide's "round to 2 decimals only when generating the PDF" rule is applied (§5).
 *
 * <p>Unlike {@code IpPurchaseOrderReportDTO}, the document date is taken from the invoice itself
 * ({@code issuedAt}, falling back to {@code createdAt}) and never from {@code now()}: reprinting an
 * issued invoice must yield the same document.</p>
 */
@Getter
public class InvoiceReportDTO {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH);

    private String number;
    private String status;
    private String draft;
    private String date;
    private String dueDate;
    private String currency;
    private String paymentTerms;
    private String incoterms;
    private String via;
    private String orderNumber;
    private String awbBl;
    private String packingList;
    private String remarks;

    private String clientName;
    private String clientTaxId;
    private String clientAddress;
    private String clientCity;
    private String clientPhone;
    private String clientContactName;
    private String clientContactEmail;

    private String shipToName;
    private String shipToAddress;
    private String shipToCity;
    private String shipToPhone;
    private String shipToContactName;
    private String shipToEmail;

    private String productsTotal;
    private String chargesTotal;
    private String taxesTotal;
    private String total;
    private String paidAmount;
    private String balanceDue;

    private JRBeanCollectionDataSource products;
    private JRBeanCollectionDataSource charges;
    private JRBeanCollectionDataSource taxes;

    private InvoiceReportDTO() {}

    public InvoiceReportDTO(InvoiceDTO invoice) {
        var documentDate = invoice.getIssuedAt() != null ? invoice.getIssuedAt() : invoice.getCreatedAt();

        this.number = formatNumber(invoice);
        this.status = invoice.getStatus() != null ? invoice.getStatus().getName() : "";
        // Lets the template stamp a "DRAFT" mark: a non-issued invoice is not a legal document.
        this.draft = invoice.getStatus() == InvoiceStatus.DRAFT ? "DRAFT" : "";
        this.date = formatDate(documentDate);
        this.dueDate = formatDate(invoice.getDueAt());
        this.currency = invoice.getCurrency() != null ? invoice.getCurrency().getName() : "";
        this.paymentTerms = invoice.getPaymentTerms() != null ? invoice.getPaymentTerms().getName() : "";
        this.incoterms = invoice.getIncoterms() != null ? invoice.getIncoterms().getName() : "";
        this.via = invoice.getVia() != null ? invoice.getVia().getName() : "";
        this.orderNumber = orEmpty(invoice.getOrderNumber());
        this.awbBl = orEmpty(invoice.getAwbBl());
        this.packingList = orEmpty(invoice.getPackingList());
        // internalRemarks stays out on purpose: it is not meant for the client.
        this.remarks = orEmpty(invoice.getRemarks());

        configClient(invoice);
        configShipTo(invoice);
        configLineItems(invoice);
        configTotals(invoice);
    }

    /**
     * An issued invoice prints its official number; a draft prints its draft number, paired with
     * the {@code draft} mark so the two can never be mistaken for each other.
     */
    private String formatNumber(InvoiceDTO invoice) {
        var formatted = InvoiceNumberFormatter.officialOrDraft(invoice.getNumber(), invoice.getDraftNumber());
        return formatted != null ? formatted : "";
    }

    private void configClient(InvoiceDTO invoice) {
        ClientDTO client = invoice.getClient();
        if (client == null) return;

        this.clientName = orEmpty(client.getShowName() != null ? client.getShowName() : client.getName());
        this.clientTaxId = orEmpty(client.getTaxId());
        this.clientAddress = orEmpty(client.getAddress());
        this.clientCity = client.getCity() != null ? orEmpty(client.getCity().getFullName()) : "";
        this.clientPhone = formatPhone(client.getCountryCode(), client.getCityCode(), client.getPhoneNumber());

        var contact = invoice.getClientContact();
        if (contact != null) {
            this.clientContactName = orEmpty(contact.getName());
            this.clientContactEmail = orEmpty(contact.getEmail());
        }
    }

    private void configShipTo(InvoiceDTO invoice) {
        this.shipToName = orEmpty(invoice.getShipToName());
        this.shipToAddress = orEmpty(invoice.getShipToAddress());
        this.shipToCity = invoice.getShipToCity() != null ? orEmpty(invoice.getShipToCity().getFullName()) : "";
        this.shipToPhone = orEmpty(invoice.getShipToPhone());
        this.shipToContactName = orEmpty(invoice.getShipToContactName());
        this.shipToEmail = orEmpty(invoice.getShipToEmail());
    }

    private void configLineItems(InvoiceDTO invoice) {
        List<InvoiceProductReportDTO> productLines = new ArrayList<>();
        int index = 1;
        for (var product : Optional.ofNullable(invoice.getProducts()).orElseGet(Collections::emptyList)) {
            productLines.add(new InvoiceProductReportDTO(index, product));
            index++;
        }
        this.products = new JRBeanCollectionDataSource(productLines);

        this.charges = new JRBeanCollectionDataSource(
                Optional.ofNullable(invoice.getCharges()).orElseGet(Collections::emptyList).stream()
                        .map(InvoiceChargeReportDTO::new).toList());

        this.taxes = new JRBeanCollectionDataSource(
                Optional.ofNullable(invoice.getTaxes()).orElseGet(Collections::emptyList).stream()
                        .map(InvoiceTaxReportDTO::new).toList());
    }

    private void configTotals(InvoiceDTO invoice) {
        DecimalFormat format = new DecimalFormat("#,##0.00");
        var total = InvoiceMoney.orZero(invoice.getTotalAmount());
        var paid = InvoiceMoney.orZero(invoice.getPaidAmount());

        this.productsTotal = format.format(invoice.getProductsTotal());
        this.chargesTotal = format.format(invoice.getChargesTotal());
        this.taxesTotal = format.format(invoice.getTaxesTotal());
        this.total = format.format(total);
        this.paidAmount = format.format(paid);
        this.balanceDue = format.format(total.subtract(paid));
    }

    private String formatPhone(String countryCode, String cityCode, String phoneNumber) {
        var phone = new StringBuilder();
        if (countryCode != null) phone.append("+").append(countryCode).append(" ");
        if (cityCode != null) phone.append("(").append(cityCode).append(") ");
        if (phoneNumber != null && phoneNumber.length() >= 4)
            phone.append(phoneNumber, 0, 3).append("-").append(phoneNumber.substring(3));
        else if (phoneNumber != null)
            phone.append(phoneNumber);
        return phone.toString();
    }

    private String formatDate(ZonedDateTime date) {
        return date != null ? date.format(DATE_FORMAT) : "";
    }

    private String orEmpty(String value) {
        return value != null ? value : "";
    }
}
