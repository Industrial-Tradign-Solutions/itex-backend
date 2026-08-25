package com.itradingsolutions.itex.api.sales.invoices.models.dto.reports;

import com.itradingsolutions.itex.api.sales.invoices.models.InvoiceMoney;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceProductDTO;
import lombok.Getter;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * One billed line. {@code unitPrice} on the invoice is the raw cost, so the printed price is the
 * selling price — cost plus margin — and the extended price is what
 * {@code InvoiceProductDTO.getSellingExtendedPrice} already computes.
 */
@Getter
public class InvoiceProductReportDTO {

    private String number;
    private String quantity;
    private String unitType;
    private String description;
    private String reference;
    private String condition;
    private String deliveryTime;
    private String unitPrice;
    private String extendedPrice;

    private InvoiceProductReportDTO() {}

    public InvoiceProductReportDTO(int number, InvoiceProductDTO product) {
        DecimalFormat amountFormat = new DecimalFormat("#,##0.00");
        DecimalFormat quantityFormat = new DecimalFormat("#,##0.00000");

        this.number = String.valueOf(number);
        this.quantity = quantityFormat.format(InvoiceMoney.orZero(product.getQuantity()));
        this.unitType = product.getUnitType() != null ? product.getUnitType() : "";
        this.condition = product.getCondition() != null ? product.getCondition().getName() : "";
        this.unitPrice = quantityFormat.format(InvoiceMoney.sellingUnitPrice(product.getUnitPrice(), product.getProfitMargin()));
        this.extendedPrice = amountFormat.format(product.getSellingExtendedPrice());

        configProduct(product);
        configDeliveryTime(product);
    }

    private void configProduct(InvoiceProductDTO product) {
        var ipProduct = product.getProduct();
        if (ipProduct == null) {
            this.description = "";
            this.reference = "";
            return;
        }
        this.description = firstNonBlank(ipProduct.getDescription(), ipProduct.getClientDescription());
        this.reference = firstNonBlank(ipProduct.getMfrReference(), ipProduct.getClientReference());
    }

    private void configDeliveryTime(InvoiceProductDTO product) {
        var deliveryTime = new StringBuilder();
        if (product.getLeadTime() != null) deliveryTime.append(product.getLeadTime());
        if (product.getLeadTimeType() != null) {
            if (!deliveryTime.isEmpty()) deliveryTime.append(" ");
            deliveryTime.append(product.getLeadTimeType().getName());
        }
        this.deliveryTime = deliveryTime.toString();
    }

    private String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) return first;
        return second != null ? second : "";
    }
}
