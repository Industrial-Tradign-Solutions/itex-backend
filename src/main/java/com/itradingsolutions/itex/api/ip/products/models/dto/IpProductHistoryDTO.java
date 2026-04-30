package com.itradingsolutions.itex.api.ip.products.models.dto;

import com.itradingsolutions.itex.api.common.models.dto.HistoryDTO;
import com.itradingsolutions.itex.api.ip.products.models.enums.IpProductHistoryActions;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class IpProductHistoryDTO extends HistoryDTO {
    private UUID product;
    private IpProductHistoryActions action;

    public String getEmployee() {
        return getUser().getFullName();
    }
}
