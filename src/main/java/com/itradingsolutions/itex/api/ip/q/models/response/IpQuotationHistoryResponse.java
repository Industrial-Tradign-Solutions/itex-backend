package com.itradingsolutions.itex.api.ip.q.models.response;

import com.itradingsolutions.itex.api.common.models.responses.HistoryResponse;
import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationHistoryAction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Response DTO for IP Quotation history.
 */
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class IpQuotationHistoryResponse extends HistoryResponse {
    private IpQuotationHistoryAction action;
}
