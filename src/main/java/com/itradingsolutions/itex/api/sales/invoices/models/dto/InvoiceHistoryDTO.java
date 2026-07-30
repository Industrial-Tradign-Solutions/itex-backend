package com.itradingsolutions.itex.api.sales.invoices.models.dto;

import com.itradingsolutions.itex.api.common.models.dto.HistoryDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceHistoryAction;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class InvoiceHistoryDTO extends HistoryDTO {

    private UUID invoice;
    private InvoiceHistoryAction action;

    public String getEmployee() {
        return getUser().getFullName();
    }
}
