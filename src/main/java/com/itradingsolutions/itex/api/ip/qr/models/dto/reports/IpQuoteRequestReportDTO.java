package com.itradingsolutions.itex.api.ip.qr.models.dto.reports;

import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestDTO;
import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestProductDTO;
import com.itradingsolutions.itex.api.partners.suppliers.models.dto.SupplierContactDTO;
import com.itradingsolutions.itex.api.partners.suppliers.models.dto.SupplierDTO;
import lombok.Getter;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Getter
public class IpQuoteRequestReportDTO {

    private String number;
    private String paymentTerms;
    private String fboPoint;
    private String shippingPointZipCode;
    private String freightClass;
    private String notes;
    private String date;

    private String supplierName;
    private String supplierCity;
    private String supplierAddress;
    private String supplierPhone;

    private String supplierContactName;
    private String supplierContactEmail;

    private JRBeanCollectionDataSource products;

    private IpQuoteRequestReportDTO() {}

    public IpQuoteRequestReportDTO(IpQuoteRequestDTO ipQuoteRequest) {
        ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH);
        this.date = now.format(formatter);

        this.number = ipQuoteRequest.getNumber();
        this.paymentTerms = ipQuoteRequest.getPaymentTerms() != null ?  ipQuoteRequest.getPaymentTerms().getName() : "";
        this.fboPoint = ipQuoteRequest.getFobShippingPoint() != null ? ipQuoteRequest.getFobShippingPoint() : "";
        this.shippingPointZipCode =  ipQuoteRequest.getShippingPointZipCode() != null ? ipQuoteRequest.getShippingPointZipCode() : "";
        this.freightClass = ipQuoteRequest.getFreightClass() != null ?ipQuoteRequest.getFreightClass().getName() : "";
        this.notes = ipQuoteRequest.getRemarks() != null ? ipQuoteRequest.getRemarks() : "";
        this.configSupplier(ipQuoteRequest.getSupplier());
        this.configSupplierContact(ipQuoteRequest.getSupplierContact());
        this.configProducts(ipQuoteRequest.getProducts());
    }

    private void configProducts(List<IpQuoteRequestProductDTO> products) {
        this.products = new JRBeanCollectionDataSource(new ArrayList<>());
        if (products == null) return;
        List<IpQuoteRequestProductReportDTO> list = new ArrayList<>();
        int index = 1;
        for (IpQuoteRequestProductDTO prod: products) {
            list.add(new IpQuoteRequestProductReportDTO(index, prod));
            index += 1;
        }
        this.products = new JRBeanCollectionDataSource(list);
    }

    private void configSupplier(SupplierDTO supplier) {
        if (supplier == null) return;

        this.supplierName = supplier.getName();
        this.supplierAddress = supplier.getAddress() != null ? supplier.getAddress() : "";
        this.supplierCity =  supplier.getCity() != null ? supplier.getCity().getFullName() : "";
        if (supplier.getCountryCode() != null)
            this.supplierPhone =  "+" + supplier.getCountryCode() + " ";
        if (supplier.getCityCode() != null)
            this.supplierPhone +=  "(" + supplier.getCityCode() + ") ";
        if (supplier.getPhoneNumber() != null)
            this.supplierPhone +=  supplier.getPhoneNumber().substring(0, 3) + "-" + supplier.getPhoneNumber().substring(3);
    }

    private void configSupplierContact(SupplierContactDTO supplierContact) {
        if (supplierContact == null) return;
        this.supplierContactName = supplierContact.getName() != null ? supplierContact.getName() : "";
        this.supplierContactEmail = supplierContact.getEmail() != null ? supplierContact.getEmail() : "";
    }
}
