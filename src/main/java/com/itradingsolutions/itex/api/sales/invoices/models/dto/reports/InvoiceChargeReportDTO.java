package com.itradingsolutions.itex.api.sales.invoices.models.dto.reports;

import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceChargeDTO;
import lombok.Getter;

import java.math.BigDecimal;
import java.text.DecimalFormat;

@Getter
public class InvoiceChargeReportDTO {

    private String description;
    private String type;
    private String value;

    private InvoiceChargeReportDTO() {}

    public InvoiceChargeReportDTO(InvoiceChargeDTO charge) {
        DecimalFormat format = new DecimalFormat("#,##0.00");
        this.description = charge.getDescription() != null ? charge.getDescription() : "";
        this.type = charge.getType() != null ? charge.getType().getName() : "";
        this.value = format.format(charge.getValue() != null ? charge.getValue() : BigDecimal.ZERO);
    }
}
