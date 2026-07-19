package com.itradingsolutions.itex.api.ip.po.models.dto.reports;

import lombok.Getter;

import java.math.BigDecimal;
import java.text.DecimalFormat;

@Getter
public class IpPurchaseOrderOtherChargeReportDTO {

    private String description;
    private String value;

    private IpPurchaseOrderOtherChargeReportDTO() {}

    public IpPurchaseOrderOtherChargeReportDTO(String description, BigDecimal value) {
        this.description = description != null ? description : "";
        DecimalFormat format = new DecimalFormat("#,##0.00");
        this.value = value != null ? format.format(value) : format.format(BigDecimal.ZERO);
    }
}
