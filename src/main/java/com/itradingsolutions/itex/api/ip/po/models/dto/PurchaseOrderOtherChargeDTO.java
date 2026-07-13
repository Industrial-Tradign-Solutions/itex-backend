package com.itradingsolutions.itex.api.ip.po.models.dto;

import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PurchaseOrderOtherChargeDTO extends BaseDTO {

    private BigDecimal value = BigDecimal.ZERO;
    private String description;

    public void setDescription(String description) {
        if (description != null)
            this.description = capitalize(description);
    }
}
