package com.itradingsolutions.itex.api.ip.q.models.dto.reports;

import com.itradingsolutions.itex.api.common.util.ReportFormatUtil;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class IpQuotationOtherChargeReportDTO {

    private String description;
    private String value;

    private IpQuotationOtherChargeReportDTO() {}

    public IpQuotationOtherChargeReportDTO(String description, BigDecimal value) {
        this.description = description != null ? description : "";
        this.value = ReportFormatUtil.money(value);
    }

    /**
     * Creates an empty charge line (blank description and value) used as a
     * placeholder in the report when the Quotation has no other charges.
     */
    public static IpQuotationOtherChargeReportDTO blank() {
        var report = new IpQuotationOtherChargeReportDTO();
        report.description = "";
        report.value = "";
        return report;
    }
}
