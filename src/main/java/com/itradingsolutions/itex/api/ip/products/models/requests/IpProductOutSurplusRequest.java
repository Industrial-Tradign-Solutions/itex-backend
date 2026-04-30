package com.itradingsolutions.itex.api.ip.products.models.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class IpProductOutSurplusRequest {

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.00",  message = "The minimum value of the target must be 0.00")
    private BigDecimal quantity;


    public BigDecimal getPrice() {
        return new BigDecimal("0.000");
    }

    public String getWhNumber() {
        return "N/A";
    }

    public String getLocation() {
        return "N/A";
    }
}
