package com.itradingsolutions.itex.api.ip.po.models.dto;

import com.itradingsolutions.itex.api.common.models.dto.HistoryDTO;
import com.itradingsolutions.itex.api.ip.po.models.enums.IpPurchaseOrderHistoryAction;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class IpPurchaseOrderHistoryDTO extends HistoryDTO {

    private UUID ipPurchaseOrder;
    private IpPurchaseOrderHistoryAction action;

    public String getEmployee() {
        return getUser().getFullName();
    }
}
