package com.itradingsolutions.itex.api.ip.qr.models.dto;

import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;
import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.common.util.models.enums.UnitType;
import com.itradingsolutions.itex.api.ip.products.models.dto.IpProductDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Getter
@Setter
@ToString
public class IpQuoteRequestProductDTO extends BaseDTO {

    private IpProductDTO ipProduct;
    private Integer number;
    private BigDecimal quantity;
    private UnitType unitType ;
    private Integer leadTime ;
    private LeadTime leadTimeType;
    private BigDecimal unitPrice ;

    public void setUnitPrice(BigDecimal unitPrice) {
        if (unitPrice != null)
            this.unitPrice = unitPrice.setScale(5, RoundingMode.HALF_UP);
    }

    public BigDecimal getExtendedPrice() {
        if (unitPrice == null || quantity == null)
            return BigDecimal.ZERO;
        if (BigDecimal.ZERO.compareTo(unitPrice) == 0 || BigDecimal.ZERO.compareTo(quantity) == 0)
            return BigDecimal.ZERO;
        return unitPrice.multiply(quantity).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getGrossWeightLbs() {
        if (this.ipProduct == null || this.ipProduct.getNetWeightLbs() == null || quantity == null)
            return BigDecimal.ZERO;
        if (BigDecimal.ZERO.compareTo(this.ipProduct.getNetWeightLbs()) == 0 || BigDecimal.ZERO.compareTo(quantity) == 0)
            return BigDecimal.ZERO;
        return this.ipProduct.getNetWeightLbs().multiply(quantity).setScale(2, RoundingMode.HALF_UP);
    }

    public void setProductId(UUID productId) {
        if (productId != null) {
            this.ipProduct = new IpProductDTO();
            this.ipProduct.setId(productId);
        }
    }
}
