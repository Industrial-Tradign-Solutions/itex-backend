package com.itradingsolutions.itex.api.ip.q.models.dto.reports;

import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationProductDTO;
import lombok.Getter;

import java.math.BigDecimal;
import java.text.DecimalFormat;

@Getter
public class IpQuotationProductReportDTO {

    private String number;
    private String quantity;
    private String unit;
    private String clientDescription = "";
    private String clientRef = "";
    private String leadTime;
    private String condition;
    private String unitPrice;
    private String extendedPrice;

    private IpQuotationProductReportDTO() {}

    public IpQuotationProductReportDTO(Integer number, IpQuotationProductDTO product) {
        this.number = number.toString();

        var qrProduct = product.getQuoteRequestProduct();
        if (qrProduct == null) {
            this.quantity = "0.00000";
            this.unit = "";
            this.leadTime = "";
            this.condition = product.getCondition() != null ? product.getCondition().getName() : "";
            DecimalFormat priceFormat = new DecimalFormat("#,##0.00000");
            DecimalFormat extPriceFormat = new DecimalFormat("#,##0.00");
            this.unitPrice = priceFormat.format(BigDecimal.ZERO);
            this.extendedPrice = extPriceFormat.format(BigDecimal.ZERO);
            return;
        }

        DecimalFormat qtyFormat = new DecimalFormat("#,##0.00000");
        this.quantity = qrProduct.getQuantity() != null ? qtyFormat.format(qrProduct.getQuantity()) : "0.00000";
        this.unit = qrProduct.getUnitType() != null ? qrProduct.getUnitType().getName() : "";

        var ipProduct = qrProduct.getIpProduct();
        if (ipProduct != null) {
            this.clientDescription = ipProduct.getClientDescription() != null ? ipProduct.getClientDescription() : "";
            this.clientRef = ipProduct.getClientReference() != null ? ipProduct.getClientReference() : "";
        }

        if (qrProduct.getLeadTime() != null && qrProduct.getLeadTimeType() != null) {
            this.leadTime = qrProduct.getLeadTime() + " " + qrProduct.getLeadTimeType().getName();
        } else {
            this.leadTime = "";
        }

        this.condition = product.getCondition() != null ? product.getCondition().getName() : "";

        DecimalFormat priceFormat = new DecimalFormat("#,##0.00000");
        DecimalFormat extPriceFormat = new DecimalFormat("#,##0.00");
        this.unitPrice = priceFormat.format(product.getSellingUnitPrice());
        this.extendedPrice = extPriceFormat.format(product.getSellingExtendedPrice());
    }
}
