package com.itradingsolutions.itex.api.ip.po.models.dto;

import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationOtherChargesQuoteRequestDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IpPurchaseOrderOtherChargesQuotationQrDTO extends BaseDTO {

    private IpQuotationOtherChargesQuoteRequestDTO quotationQrOtherCharge;
}
