package com.itradingsolutions.itex.api.ip.qr.models.dto.reports;
import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestProductDTO;
import lombok.Getter;

import java.text.DecimalFormat;

@Getter
public class IpQuoteRequestProductReportDTO {
    private String number;
    private String quantity;
    private String description = "";
    private String reference = "";
    private String unitType ;

    private IpQuoteRequestProductReportDTO() {}

    public IpQuoteRequestProductReportDTO(Integer number, IpQuoteRequestProductDTO product) {
        this.number = number.toString();
        DecimalFormat format = new DecimalFormat("#,##0.00000");
        this.quantity = format.format(product.getQuantity());
        this.unitType = product.getUnitType().getName();
        this.description = product.getIpProduct().getDescription();
        this.reference = product.getIpProduct().getMfrReference()  != null ? product.getIpProduct().getMfrReference() : "";
    }
}
