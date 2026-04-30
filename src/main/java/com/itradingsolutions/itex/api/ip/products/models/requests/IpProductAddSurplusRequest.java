package com.itradingsolutions.itex.api.ip.products.models.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class IpProductAddSurplusRequest {

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.00",  message = "The minimum value of the target must be 0.00")
    private BigDecimal quantity;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00",  message = "The minimum value of the target must be 0.00")
    private BigDecimal price;

    @NotEmpty(message = "WH Number is required")
    private String whNumber;

    @NotEmpty(message = "Location is required")
    private String location;

}
