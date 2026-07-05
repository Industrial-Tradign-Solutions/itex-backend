package com.itradingsolutions.itex.api.ip.q.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IpQuotationsQuoteRequestSummaryDTO {

    /** The junction (IpQuotationsQuoteRequest) id — used for DELETE /quote-request/{qqrId} */
    private UUID qqrId;
    /** The QR's own id — used for opening the QR or loading its products */
    private UUID id;
    private String number;
    private BigDecimal freightCharges;
}
