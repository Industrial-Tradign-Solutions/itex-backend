package com.itradingsolutions.itex.api.sales.invoices.models.dto;

import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Reference to a Purchase Order linked to an Invoice. Only what the UI shows in the link list;
 * {@code id} comes from {@link BaseDTO}.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class InvoicePurchaseOrderDTO extends BaseDTO {

    private String number;
}
