package com.itradingsolutions.itex.api.ip.po.models.dto.reports;

import com.itradingsolutions.itex.api.ip.po.models.dto.IpPurchaseOrderDTO;
import com.itradingsolutions.itex.api.ip.po.models.dto.IpPurchaseOrderProductDTO;
import com.itradingsolutions.itex.api.partners.suppliers.models.dto.SupplierContactDTO;
import com.itradingsolutions.itex.api.partners.suppliers.models.dto.SupplierDTO;
import lombok.Getter;
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
public class IpPurchaseOrderReportDTO {

    private String number;
    private String date;
    private String quotationNumber;
    private String shippingMethod;
    private String leadTime;
    private String paymentTerms;
    private String notes;

    private String supplierName;
    private String supplierCity;
    private String supplierAddress;
    private String supplierPhone;
    private String supplierContactName;
    private String supplierContactEmail;

    private String shipToName;
    private String shipToCity;
    private String shipToAddress;
    private String shipToPhone;
    private String shipToContactName;
    private String shipToEmail;

    private String subTotal;
    private String salesTax;
    private String totalOtherCharges;
    private String total;

    private JRBeanCollectionDataSource products;
    private JRBeanCollectionDataSource otherCharges;

    private IpPurchaseOrderReportDTO() {}

    public IpPurchaseOrderReportDTO(IpPurchaseOrderDTO po) {
        ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH);
        this.date = now.format(formatter);

        this.number = po.getNumber() != null ? po.getNumber() : "";
        this.quotationNumber = po.getQuotation() != null && po.getQuotation().getNumber() != null
                ? po.getQuotation().getNumber() : "";
        this.shippingMethod = po.getShippingMethod() != null ? po.getShippingMethod() : "";
        this.paymentTerms = po.getPaymentTerms() != null ? po.getPaymentTerms().getName() : "";
        this.notes = po.getRemarks() != null ? po.getRemarks() : "";

        configLeadTime(po);
        configSupplier(po.getSupplier());
        configSupplierContact(po.getSupplierContact());
        configShipTo(po);
        configProducts(po.getProducts());
        configOtherCharges(po);
        configTotals(po);
    }

    private void configLeadTime(IpPurchaseOrderDTO po) {
        var leadTime = new StringBuilder();
        if (po.getLeadTime() != null) leadTime.append(po.getLeadTime());
        if (po.getLeadTimeType() != null) {
            if (!leadTime.isEmpty()) leadTime.append(" ");
            leadTime.append(po.getLeadTimeType().getName());
        }
        this.leadTime = leadTime.toString();
    }

    private void configSupplier(SupplierDTO supplier) {
        if (supplier == null) return;

        this.supplierName = supplier.getName() != null ? supplier.getName() : "";
        this.supplierAddress = supplier.getAddress() != null ? supplier.getAddress() : "";
        this.supplierCity = supplier.getCity() != null ? supplier.getCity().getFullName() : "";

        var phone = new StringBuilder();
        if (supplier.getCountryCode() != null)
            phone.append("+").append(supplier.getCountryCode()).append(" ");
        if (supplier.getCityCode() != null)
            phone.append("(").append(supplier.getCityCode()).append(") ");
        if (supplier.getPhoneNumber() != null && supplier.getPhoneNumber().length() >= 4)
            phone.append(supplier.getPhoneNumber(), 0, 3).append("-").append(supplier.getPhoneNumber().substring(3));
        else if (supplier.getPhoneNumber() != null)
            phone.append(supplier.getPhoneNumber());
        this.supplierPhone = phone.toString();
    }

    private void configSupplierContact(SupplierContactDTO supplierContact) {
        if (supplierContact == null) return;
        this.supplierContactName = supplierContact.getName() != null ? supplierContact.getName() : "";
        this.supplierContactEmail = supplierContact.getEmail() != null ? supplierContact.getEmail() : "";
    }

    private void configShipTo(IpPurchaseOrderDTO po) {
        this.shipToName = po.getShipToName() != null ? po.getShipToName() : "";
        this.shipToAddress = po.getShipToAddress() != null ? po.getShipToAddress() : "";
        this.shipToCity = po.getShipToCity() != null ? po.getShipToCity().getFullName() : "";
        this.shipToPhone = po.getShipToPhone() != null ? po.getShipToPhone() : "";
        this.shipToContactName = po.getShipToContactName() != null ? po.getShipToContactName() : "";
        this.shipToEmail = po.getShipToEmail() != null ? po.getShipToEmail() : "";
    }

    private void configProducts(List<IpPurchaseOrderProductDTO> products) {
        List<IpPurchaseOrderProductReportDTO> list = new ArrayList<>();
        if (products != null) {
            int index = 1;
            for (IpPurchaseOrderProductDTO prod : products) {
                list.add(new IpPurchaseOrderProductReportDTO(index, prod));
                index++;
            }
        }
        this.products = new JRBeanCollectionDataSource(list);
    }

    private void configOtherCharges(IpPurchaseOrderDTO po) {
        List<IpPurchaseOrderOtherChargeReportDTO> list = new ArrayList<>();
        Optional.ofNullable(po.getAllOtherCharges())
                .ifPresent(charges -> charges.forEach(oc ->
                        list.add(new IpPurchaseOrderOtherChargeReportDTO(oc.description(), oc.value()))
                ));
        this.otherCharges = new JRBeanCollectionDataSource(list);
    }

    private void configTotals(IpPurchaseOrderDTO po) {
        DecimalFormat format = new DecimalFormat("#,##0.00");
        this.subTotal = format.format(po.getSubTotal());
        this.salesTax = po.getSalesTax() != null ? format.format(po.getSalesTax()) : format.format(BigDecimal.ZERO);
        this.totalOtherCharges = format.format(po.getTotalOtherCharges());
        this.total = format.format(po.getTotal());
    }
}
