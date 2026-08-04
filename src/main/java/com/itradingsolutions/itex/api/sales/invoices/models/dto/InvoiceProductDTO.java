package com.itradingsolutions.itex.api.sales.invoices.models.dto;

import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;
import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.ip.products.models.dto.IpProductDTO;
import com.itradingsolutions.itex.api.ip.products.models.enums.ProductCondition;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class InvoiceProductDTO extends BaseDTO {

    private IpProductDTO product;
    private Integer number = 1;
    private BigDecimal quantity = BigDecimal.ZERO;
    private String unitType;
    private Integer leadTime = 0;
    private LeadTime leadTimeType;
    private BigDecimal unitPrice = BigDecimal.ZERO;
    private BigDecimal profitMargin = BigDecimal.ZERO;
    private ProductCondition condition;

    /**
     * Billed line total. {@code unitPrice} is the raw cost dragged from the Quote Request and
     * {@code profitMargin} the margin dragged from the Quotation; the selling price is recomputed
     * here once as {@code cost * (1 + margin)} so the margin is never applied twice — what was
     * quoted is what gets invoiced. Mirrors {@code IpQuotationProductDTO.getSellingExtendedPrice}.
     */
    public BigDecimal getExtendedPrice() {
        if (quantity == null || unitPrice == null)
            return BigDecimal.ZERO;
        var margin = profitMargin != null ? profitMargin : BigDecimal.ZERO;
        return quantity.multiply(unitPrice)
                .multiply(BigDecimal.ONE.add(margin))
                .setScale(5, RoundingMode.HALF_UP);
    }

    public void setUnitType(String unitType) {
        if (unitType != null)
            this.unitType = unitType.trim();
    }

    public void setProductId(UUID productId) {
        if (productId != null) {
            this.product = new IpProductDTO();
            this.product.setId(productId);
        }
    }
}
