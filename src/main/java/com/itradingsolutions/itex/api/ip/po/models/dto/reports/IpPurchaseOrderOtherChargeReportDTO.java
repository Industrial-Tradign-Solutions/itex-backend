package com.itradingsolutions.itex.api.ip.po.models.dto.reports;

import com.itradingsolutions.itex.api.common.util.ReportFormatUtil;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class IpPurchaseOrderOtherChargeReportDTO {

    private String description;
    private String value;

    private IpPurchaseOrderOtherChargeReportDTO() {}

    public IpPurchaseOrderOtherChargeReportDTO(String description, BigDecimal value) {
        this.description = description != null ? description : "";
        this.value = ReportFormatUtil.money(value);
    }
}
