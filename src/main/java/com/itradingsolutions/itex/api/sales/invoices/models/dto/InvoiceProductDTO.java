package com.itradingsolutions.itex.api.sales.invoices.models.dto;

import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;
import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.ip.products.models.dto.IpProductDTO;
import com.itradingsolutions.itex.api.ip.products.models.enums.ProductCondition;
import com.itradingsolutions.itex.api.sales.invoices.models.InvoiceMoney;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
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

    /** Raw cost of this line: {@code quantity * unitPrice}, without margin. For display only. */
    public BigDecimal getExtendedPrice() {
        return InvoiceMoney.costExtendedPrice(quantity, unitPrice);
    }

    /** Selling price of one unit: {@code unitPrice * (1 + profitMargin / 100)}. */
    public BigDecimal getSellingUnitPrice() {
        return InvoiceMoney.sellingUnitPrice(unitPrice, profitMargin);
    }

    /**
     * Selling total of this line: {@code quantity * sellingUnitPrice}. Used for invoice totals
     * ({@code productsTotal}) and for display. Mirrors the old {@code getExtendedPrice} formula.
     */
    public BigDecimal getSellingExtendedPrice() {
        return InvoiceMoney.extendedPrice(quantity, unitPrice, profitMargin);
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
