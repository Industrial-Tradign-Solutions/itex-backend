package com.itradingsolutions.itex.api.ip.po.models.dto.reports;

import com.itradingsolutions.itex.api.common.util.ReportFormatUtil;
import com.itradingsolutions.itex.api.ip.po.models.dto.IpPurchaseOrderProductDTO;
import com.itradingsolutions.itex.api.ip.products.models.dto.IpProductDTO;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationProductDTO;
import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestProductDTO;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class IpPurchaseOrderProductReportDTO {

    private String number;
    private String quantity;
    private String unitType;
    private String description = "";
    private String reference = "";
    private String deliveryTime = "";
    private String unitPrice;
    private String extendedPrice;

    private IpPurchaseOrderProductReportDTO() {}

    public IpPurchaseOrderProductReportDTO(Integer number, IpPurchaseOrderProductDTO product) {
        this.number = number.toString();

        IpQuotationProductDTO quotationProduct = product.getQuotationProduct();
        IpQuoteRequestProductDTO qrProduct = quotationProduct != null
                ? quotationProduct.getQuoteRequestProduct()
                : null;

        if (qrProduct == null) {
            this.quantity = ReportFormatUtil.quantity(BigDecimal.ZERO);
            this.unitType = "";
            this.unitPrice = ReportFormatUtil.price(BigDecimal.ZERO);
            this.extendedPrice = ReportFormatUtil.money(BigDecimal.ZERO);
            return;
        }

        this.quantity = ReportFormatUtil.quantity(qrProduct.getQuantity());
        this.unitType = qrProduct.getUnitType() != null ? qrProduct.getUnitType().getName() : "";

        configProductInfo(qrProduct.getIpProduct());
        configDeliveryTime(qrProduct);

        this.unitPrice = ReportFormatUtil.price(qrProduct.getUnitPrice());
        this.extendedPrice = ReportFormatUtil.money(qrProduct.getExtendedPrice());
    }

    private void configProductInfo(IpProductDTO ipProduct) {
        if (ipProduct == null) return;

        this.description = firstNonBlank(ipProduct.getDescription(), ipProduct.getClientDescription());
        this.reference = firstNonBlank(ipProduct.getMfrReference(), ipProduct.getClientReference());
    }

    private void configDeliveryTime(IpQuoteRequestProductDTO qrProduct) {
        if (qrProduct.getLeadTime() != null && qrProduct.getLeadTimeType() != null) {
            this.deliveryTime = qrProduct.getLeadTime() + " " + qrProduct.getLeadTimeType().getName();
        } else {
            this.deliveryTime = "";
        }
    }

    private String firstNonBlank(String primary, String fallback) {
        if (primary != null && !primary.isBlank()) return primary;
        if (fallback != null && !fallback.isBlank()) return fallback;
        return "";
    }
}
