package com.itradingsolutions.itex.api.sales.invoices.models.dto.reports;

import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceTaxDTO;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

@Getter
public class InvoiceTaxReportDTO {

    private String type;
    private String description;
    private String rate;
    private String taxableBase;
    private String value;

    private InvoiceTaxReportDTO() {}

    public InvoiceTaxReportDTO(InvoiceTaxDTO tax) {
        DecimalFormat format = new DecimalFormat("#,##0.00");
        this.type = tax.getType() != null ? tax.getType().getName() : "";
        this.description = tax.getDescription() != null ? tax.getDescription() : "";
        this.rate = ratePercentage(tax.getRate());
        this.taxableBase = format.format(tax.getTaxableBase() != null ? tax.getTaxableBase() : BigDecimal.ZERO);
        this.value = format.format(tax.getValue() != null ? tax.getValue() : BigDecimal.ZERO);
    }

    /** {@code rate} is stored as a fraction (0.1900); the document shows it as "19.00%". */
    private String ratePercentage(BigDecimal rate) {
        var percentage = (rate != null ? rate : BigDecimal.ZERO)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
        return new DecimalFormat("#,##0.00").format(percentage) + "%";
    }
}
