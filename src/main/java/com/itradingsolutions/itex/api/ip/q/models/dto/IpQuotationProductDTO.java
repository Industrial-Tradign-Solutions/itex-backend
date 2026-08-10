package com.itradingsolutions.itex.api.ip.q.models.dto;

import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;
import com.itradingsolutions.itex.api.ip.products.models.enums.ProductCondition;
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
    private ProductCondition condition;
    private String qrNumber;
    private String supplierName;

    public BigDecimal getSellingUnitPrice() {
        if (quoteRequestProduct == null || quoteRequestProduct.getUnitPrice() == null)
            return BigDecimal.ZERO;
        if (profitMargin == null || BigDecimal.ZERO.compareTo(profitMargin) == 0)
            return quoteRequestProduct.getUnitPrice();
        return quoteRequestProduct.getUnitPrice()
                .multiply(marginFactor())
                .setScale(5, RoundingMode.HALF_UP);
    }

    public BigDecimal getSellingExtendedPrice() {
        if (quoteRequestProduct == null || quoteRequestProduct.getExtendedPrice() == null)
            return BigDecimal.ZERO;
        if (profitMargin == null || BigDecimal.ZERO.compareTo(profitMargin) == 0)
            return quoteRequestProduct.getExtendedPrice();
        return quoteRequestProduct.getExtendedPrice()
                .multiply(marginFactor())
                .setScale(5, RoundingMode.HALF_UP);
    }

    /**
     * {@code profitMargin} is stored as a direct percentage (10.00 = 10%), not a fraction, so it
     * must be divided by 100 before being applied as a multiplier.
     */
    private BigDecimal marginFactor() {
        return BigDecimal.ONE.add(profitMargin.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP));
    }

    public BigDecimal getGrossWeightLbs() {
        if (quoteRequestProduct == null) return BigDecimal.ZERO;
        return quoteRequestProduct.getGrossWeightLbs();
    }
}
