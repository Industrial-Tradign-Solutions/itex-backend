package com.itradingsolutions.itex.api.ip.po.models.response;

import com.itradingsolutions.itex.api.common.models.responses.HistoryResponse;
import com.itradingsolutions.itex.api.ip.po.models.enums.PurchaseOrderHistoryAction;
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
public class PurchaseOrderHistoryResponse extends HistoryResponse {
    private PurchaseOrderHistoryAction action;
}
