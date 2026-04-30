package com.itradingsolutions.itex.api.ip.products.models.responses;

import com.itradingsolutions.itex.api.common.models.responses.BaseResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@SuperBuilder
public class IpProductSurplusResponse extends BaseResponse {

    private BigDecimal quantity;
    private BigDecimal price;
    private String whNumber;
    private String location;
}
