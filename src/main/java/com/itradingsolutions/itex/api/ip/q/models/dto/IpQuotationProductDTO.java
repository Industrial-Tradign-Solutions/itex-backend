package com.itradingsolutions.itex.api.ip.q.models.dto;

import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;
import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationProductCondition;
import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestProductDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@ToString
public class IpQuotationProductDTO extends BaseDTO {

    private static final BigDecimal HUNDRED = new BigDecimal("100");

    private UUID quotationsQuoteRequestId;
    private IpQuoteRequestProductDTO quoteRequestProduct;
    private Integer number;
    private BigDecimal profitMargin;
    private IpQuotationProductCondition condition;
    private Integer itsLeadTime = 0;
    private String qrNumber;
    private String supplierName;

    public BigDecimal getSellingUnitPrice() {
        if (quoteRequestProduct == null || quoteRequestProduct.getUnitPrice() == null)
            return BigDecimal.ZERO;
        if (profitMargin == null || BigDecimal.ZERO.compareTo(profitMargin) == 0)
            return quoteRequestProduct.getUnitPrice();
        return quoteRequestProduct.getUnitPrice().multiply(marginFactor());
    }

    public BigDecimal getSellingExtendedPrice() {
        if (quoteRequestProduct == null || quoteRequestProduct.getExtendedPrice() == null)
            return BigDecimal.ZERO;
        if (profitMargin == null || BigDecimal.ZERO.compareTo(profitMargin) == 0)
            return quoteRequestProduct.getExtendedPrice();
        return quoteRequestProduct.getExtendedPrice().multiply(marginFactor());
    }

    /**
     * {@code profitMargin} is stored as a direct percentage (10.00 = 10%), not a fraction, so it
     * must be divided by 100 before being applied as a multiplier.
     */
    private BigDecimal marginFactor() {
        return BigDecimal.ONE.add(profitMargin.divide(HUNDRED));
    }

    public BigDecimal getGrossWeightLbs() {
        if (quoteRequestProduct == null) return BigDecimal.ZERO;
        return quoteRequestProduct.getGrossWeightLbs();
    }

    /**
     * Profit on the unit price: selling unit price minus the QR purchase unit price.
     */
    public BigDecimal getUnitProfit() {
        if (quoteRequestProduct == null || quoteRequestProduct.getUnitPrice() == null)
            return BigDecimal.ZERO;
        return getSellingUnitPrice().subtract(quoteRequestProduct.getUnitPrice());
    }

    /**
     * Profit on the extended (total) price: selling extended price minus the QR purchase extended price.
     */
    public BigDecimal getTotalProfit() {
        if (quoteRequestProduct == null || quoteRequestProduct.getExtendedPrice() == null)
            return BigDecimal.ZERO;
        return getSellingExtendedPrice().subtract(quoteRequestProduct.getExtendedPrice());
    }

    /**
     * Total delivery time for the quotation line: QR base lead time plus the ITS extra time.
     */
    public Integer getTotalLeadTime() {
        return Optional
                .ofNullable(quoteRequestProduct)
                .map(IpQuoteRequestProductDTO::getLeadTime)
                .orElse(0) + Optional
                    .ofNullable(itsLeadTime)
                    .orElse(0);
    }
}
