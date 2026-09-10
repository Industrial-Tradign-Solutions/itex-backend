package com.itradingsolutions.itex.api.ip.q.models.dto.reports;

import com.itradingsolutions.itex.api.common.util.ReportFormatUtil;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationProductDTO;
import lombok.Getter;

import java.math.BigDecimal;

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
            this.quantity = ReportFormatUtil.quantity(BigDecimal.ZERO);
            this.unit = "";
            this.leadTime = "";
            this.condition = product.getCondition() != null ? product.getCondition().getName() : "";
            this.unitPrice = ReportFormatUtil.price(BigDecimal.ZERO);
            this.extendedPrice = ReportFormatUtil.money(BigDecimal.ZERO);
            return;
        }

        this.quantity = ReportFormatUtil.quantity(qrProduct.getQuantity());
        this.unit = qrProduct.getUnitType() != null ? qrProduct.getUnitType().getName() : "";

        var ipProduct = qrProduct.getIpProduct();
        if (ipProduct != null) {
            this.clientDescription = ipProduct.getClientDescription() != null ? ipProduct.getClientDescription() : "";
            this.clientRef = ipProduct.getClientReference() != null ? ipProduct.getClientReference() : "";
        }

        if (qrProduct.getLeadTimeType() != null) {
            this.leadTime = product.getTotalLeadTime() + " " + qrProduct.getLeadTimeType().getName();
        } else {
            this.leadTime = "";
        }

        this.condition = product.getCondition() != null ? product.getCondition().getName() : "";

        this.unitPrice = ReportFormatUtil.price(product.getSellingUnitPrice());
        this.extendedPrice = ReportFormatUtil.money(product.getSellingExtendedPrice());
    }
}
