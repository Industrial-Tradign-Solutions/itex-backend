package com.itradingsolutions.itex.api.ip.po.models.dto;

import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationProductDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Optional;

@Getter
@Setter
@ToString
public class IpPurchaseOrderProductDTO extends BaseDTO {

    private IpQuotationProductDTO quotationProduct;
    private Integer number;

    public BigDecimal getExtendedPrice() {
        return Optional.ofNullable(quotationProduct)
                .map(IpQuotationProductDTO::getSellingExtendedPrice)
                .orElse(BigDecimal.ZERO);
    }
}
