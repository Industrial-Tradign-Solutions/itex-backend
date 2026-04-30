package com.itradingsolutions.itex.api.ip.qr.models.dto;

import com.itradingsolutions.itex.api.common.models.dto.HistoryDTO;
import com.itradingsolutions.itex.api.ip.qr.models.enums.IpQuoteRequestHistoryAction;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class IpQuoteRequestHistoryDTO extends HistoryDTO {

    private UUID ipQuoteRequest;
    private IpQuoteRequestHistoryAction action;

    public String getEmployee() {
        return getUser().getFullName();
    }
}
