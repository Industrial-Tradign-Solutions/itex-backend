package com.itradingsolutions.itex.api.sales.invoices.models.response;

import com.itradingsolutions.itex.api.common.models.responses.HistoryResponse;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceHistoryAction;
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
public class InvoiceHistoryResponse extends HistoryResponse {
    private InvoiceHistoryAction action;
}
