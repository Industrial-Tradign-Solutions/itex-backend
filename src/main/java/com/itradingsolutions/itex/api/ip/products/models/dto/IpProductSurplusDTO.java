package com.itradingsolutions.itex.api.ip.products.models.dto;

import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;
import com.itradingsolutions.itex.api.ip.products.models.enums.IpProductSurplusType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class IpProductSurplusDTO extends BaseDTO {

    private BigDecimal quantity;
    private BigDecimal price;
    private String whNumber;
    private String location;
    private IpProductSurplusType type;

    public void setWhNumber(String whNumber) {
        this.whNumber = normalizeText(whNumber).trim().toUpperCase();
    }

    public void setLocation(String location) {
        this.location = normalizeText(location).trim().toUpperCase();
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setQuantity(String quantity) {
        this.quantity = new BigDecimal(quantity);
    }

    public void setPrice(String price) {
        this.price = new BigDecimal(price);
    }

}
