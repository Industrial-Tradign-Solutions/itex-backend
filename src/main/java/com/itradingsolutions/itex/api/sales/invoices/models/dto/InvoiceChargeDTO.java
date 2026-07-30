package com.itradingsolutions.itex.api.sales.invoices.models.dto;

import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceChargeType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class InvoiceChargeDTO extends BaseDTO {

    private String description;
    private InvoiceChargeType type;
    private BigDecimal value = BigDecimal.ZERO;

    public void setDescription(String description) {
        if (description != null)
            this.description = capitalize(description);
    }
}
