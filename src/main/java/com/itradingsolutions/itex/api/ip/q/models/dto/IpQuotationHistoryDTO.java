package com.itradingsolutions.itex.api.ip.q.models.dto;

import com.itradingsolutions.itex.api.common.models.dto.HistoryDTO;
import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationHistoryAction;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

/**
 * DTO for IP Quotation history records.
 */
@Getter
@Setter
@ToString
public class IpQuotationHistoryDTO extends HistoryDTO {

    private UUID ipQuotation;
    private IpQuotationHistoryAction action;

    /**
     * @return The full name of the user who performed the action
     */
    public String getEmployee() {
        return getUser().getFullName();
    }
}
