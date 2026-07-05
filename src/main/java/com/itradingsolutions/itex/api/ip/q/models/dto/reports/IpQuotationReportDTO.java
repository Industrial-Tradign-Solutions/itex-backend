package com.itradingsolutions.itex.api.ip.q.models.dto.reports;

import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationDTO;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationOtherChargeDTO;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationOtherChargesQuoteRequestDTO;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationProductDTO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Getter
@Slf4j
public class IpQuotationReportDTO {

    private String number;
    private String paymentTerms;
    private String notes;
    private String date;

    private String clientName;
    private String clientCity;
    private String clientAddress;
    private String clientPhone;

    private String clientContactName;
    private String clientContactEmail;

    private String validity;
    private String incoterms;
    private String leadTime;
    private String totalItems;
    private String grossWeightLbs;
    private String salesRepName;

    private String subTotal;
    private String totalOtherCharges;
    private String freightCharges;
    private String total;

    private JRBeanCollectionDataSource products;
    private JRBeanCollectionDataSource otherCharges;

    private IpQuotationReportDTO() {}

    public IpQuotationReportDTO(IpQuotationDTO quotation) {
        ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH);
        this.date = now.format(formatter);

        this.number = quotation.getNumber();
        this.paymentTerms = quotation.getPaymentTerms() != null ? quotation.getPaymentTerms().getName() : "";
        this.notes = quotation.getRemarks() != null ? quotation.getRemarks() : "";

        configClient(quotation);
        configClientContact(quotation);
        configProducts(quotation.getProducts());
        configOtherCharges(quotation);
        configTotals(quotation);
        configQFields(quotation);
    }

    private void configClient(IpQuotationDTO quotation) {
        var client = quotation.getClient();
        if (client == null) return;

        this.clientName = client.getName() != null ? client.getName() : "";
        this.clientAddress = client.getAddress() != null ? client.getAddress() : "";
        this.clientCity = client.getCity() != null ? client.getCity().getFullName() : "";

        var phone = new StringBuilder();
        if (client.getCountryCode() != null)
            phone.append("+").append(client.getCountryCode()).append(" ");
        if (client.getCityCode() != null)
            phone.append("(").append(client.getCityCode()).append(") ");
        if (client.getPhoneNumber() != null && client.getPhoneNumber().length() >= 4)
            phone.append(client.getPhoneNumber().substring(0, 3)).append("-").append(client.getPhoneNumber().substring(3));
        else if (client.getPhoneNumber() != null)
            phone.append(client.getPhoneNumber());
        this.clientPhone = phone.toString();
    }

    private void configClientContact(IpQuotationDTO quotation) {
        var contact = quotation.getClientContact();
        if (contact == null) return;
        this.clientContactName = contact.getName() != null ? contact.getName() : "";
        this.clientContactEmail = contact.getEmail() != null ? contact.getEmail() : "";
    }

    private void configProducts(List<IpQuotationProductDTO> products) {
        this.products = new JRBeanCollectionDataSource(new ArrayList<>());
        if (products == null) {
            log.warn("configProducts: products list is NULL");
            return;
        }
        log.info("configProducts: processing {} products", products.size());
        List<IpQuotationProductReportDTO> list = new ArrayList<>();
        int index = 1;
        for (IpQuotationProductDTO prod : products) {
            log.debug("configProducts: mapping product #{}: qrProduct={}, condition={}",
                    index, prod.getQuoteRequestProduct(), prod.getCondition());
            list.add(new IpQuotationProductReportDTO(index, prod));
            index++;
        }
        log.info("configProducts: final product list size = {}", list.size());
        this.products = new JRBeanCollectionDataSource(list);
    }

    private void configOtherCharges(IpQuotationDTO quotation) {
        this.otherCharges = new JRBeanCollectionDataSource(new ArrayList<>());
        List<IpQuotationOtherChargeReportDTO> list = new ArrayList<>();

        Optional.ofNullable(quotation.getOtherCharges())
                .ifPresent(charges -> charges.forEach(oc ->
                        list.add(new IpQuotationOtherChargeReportDTO(oc.getDescription(), oc.getValue()))
                ));

        Optional.ofNullable(quotation.getQrOtherCharges())
                .ifPresent(charges -> charges.forEach(oc -> {
                    var qrCharge = oc.getQrOtherCharge();
                    if (qrCharge != null) {
                        list.add(new IpQuotationOtherChargeReportDTO(
                                qrCharge.getDescription(),
                                qrCharge.getValue()
                        ));
                    }
                }));

        this.otherCharges = new JRBeanCollectionDataSource(list);
    }

    private void configTotals(IpQuotationDTO quotation) {
        DecimalFormat format = new DecimalFormat("#,##0.00");
        this.subTotal = quotation.getSubTotal() != null ? format.format(quotation.getSubTotal()) : format.format(BigDecimal.ZERO);
        this.totalOtherCharges = quotation.getTotalOtherCharges() != null ? format.format(quotation.getTotalOtherCharges()) : format.format(BigDecimal.ZERO);
        this.freightCharges = quotation.getFreightCharges() != null ? format.format(quotation.getFreightCharges()) : format.format(BigDecimal.ZERO);
        this.total = quotation.getTotal() != null ? format.format(quotation.getTotal()) : format.format(BigDecimal.ZERO);
    }

    private void configQFields(IpQuotationDTO quotation) {
        var validity = new StringBuilder();
        if (quotation.getValidity() != null) validity.append(quotation.getValidity());
        if (quotation.getValidityType() != null) {
            if (!validity.isEmpty()) validity.append(" ");
            validity.append(quotation.getValidityType().getName());
        }
        this.validity = validity.toString();

        var incoterms = quotation.getIncoterms();
        if (incoterms != null) {
            this.incoterms = incoterms.getName();
        } else {
            this.incoterms = "";
        }

        var leadTime = new StringBuilder();
        if (quotation.getLeadTime() != null) leadTime.append(quotation.getLeadTime());
        if (quotation.getLeadTimeType() != null) {
            if (!leadTime.isEmpty()) leadTime.append(" ");
            leadTime.append(quotation.getLeadTimeType().getName());
        }
        this.leadTime = leadTime.toString();

        var products = quotation.getProducts();
        this.totalItems = products != null ? String.valueOf(products.size()) : "0";

        DecimalFormat weightFormat = new DecimalFormat("#,##0.00");
        this.grossWeightLbs = weightFormat.format(quotation.getGrossWeightLbs());

        if (quotation.getSalesRep() != null) {
            this.salesRepName = quotation.getSalesRep().getFullName() != null
                    ? quotation.getSalesRep().getFullName()
                    : "";
        } else {
            this.salesRepName = "";
        }
    }
}
