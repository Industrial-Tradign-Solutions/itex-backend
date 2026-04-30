package com.itradingsolutions.itex.api.ip.products.models.responses;

import com.itradingsolutions.itex.api.common.models.responses.HistoryResponse;
import com.itradingsolutions.itex.api.ip.products.models.enums.IpProductHistoryActions;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class IpProductHistoryResponse extends HistoryResponse {
    private IpProductHistoryActions action;

}
