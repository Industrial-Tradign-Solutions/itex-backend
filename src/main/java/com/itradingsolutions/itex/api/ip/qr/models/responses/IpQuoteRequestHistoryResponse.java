package com.itradingsolutions.itex.api.ip.qr.models.responses;

import com.itradingsolutions.itex.api.common.models.responses.HistoryResponse;
import com.itradingsolutions.itex.api.ip.qr.models.enums.IpQuoteRequestHistoryAction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class IpQuoteRequestHistoryResponse extends HistoryResponse {
    private IpQuoteRequestHistoryAction action;
}
