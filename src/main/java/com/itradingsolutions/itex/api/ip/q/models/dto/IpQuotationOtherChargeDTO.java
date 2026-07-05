package com.itradingsolutions.itex.api.ip.q.models.dto;

import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * Data Transfer Object for Quotation Other Charges.
 * <p>
 * Represents an additional charge (fee, cost) associated with a quotation.
 * Used for transferring other charge data between application layers.
 * </p>
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class IpQuotationOtherChargeDTO extends BaseDTO {

    /**
     * The monetary value of the charge.
     */
    private BigDecimal value = BigDecimal.ZERO;

    /**
     * Description of the charge (e.g., "Shipping", "Handling").
     */
    private String description;

    /**
     * Sets the description, automatically capitalizing the first letter.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        if (description != null)
            this.description = capitalize(description);
    }
}
