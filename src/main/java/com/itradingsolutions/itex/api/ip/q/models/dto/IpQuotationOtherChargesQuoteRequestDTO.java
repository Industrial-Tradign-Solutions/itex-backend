package com.itradingsolutions.itex.api.ip.q.models.dto;

import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;
import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestOtherChargesDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class IpQuotationOtherChargesQuoteRequestDTO extends BaseDTO {

    private UUID quotationsQuoteRequestId;
    private IpQuoteRequestOtherChargesDTO qrOtherCharge;
}
