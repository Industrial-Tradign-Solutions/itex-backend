package com.itradingsolutions.itex.api.ip.q.models.dto;

import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;
import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationProductCondition;
import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestProductDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Getter
@Setter
@ToString
public class IpQuotationProductDTO extends BaseDTO {

    private UUID quotationsQuoteRequestId;
    private IpQuoteRequestProductDTO quoteRequestProduct;
    private Integer number;
    private BigDecimal profitMargin;
    private IpQuotationProductCondition condition;

    public BigDecimal getSellingUnitPrice() {
        if (quoteRequestProduct == null || quoteRequestProduct.getUnitPrice() == null)
            return BigDecimal.ZERO;
        if (profitMargin == null || BigDecimal.ZERO.compareTo(profitMargin) == 0)
            return quoteRequestProduct.getUnitPrice();
        return quoteRequestProduct.getUnitPrice()
                .multiply(BigDecimal.ONE.add(profitMargin))
                .setScale(5, RoundingMode.HALF_UP);
    }

    public BigDecimal getExtendedPrice() {
        if (quoteRequestProduct == null || quoteRequestProduct.getQuantity() == null)
            return BigDecimal.ZERO;
        BigDecimal sellingPrice = getSellingUnitPrice();
        if (BigDecimal.ZERO.compareTo(sellingPrice) == 0)
            return BigDecimal.ZERO;
        return sellingPrice.multiply(quoteRequestProduct.getQuantity())
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getGrossWeightLbs() {
        if (quoteRequestProduct == null) return BigDecimal.ZERO;
        return quoteRequestProduct.getGrossWeightLbs();
    }
}
