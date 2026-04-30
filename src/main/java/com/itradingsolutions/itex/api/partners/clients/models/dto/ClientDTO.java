package com.itradingsolutions.itex.api.partners.clients.models.dto;

import com.itradingsolutions.itex.api.admin.user.models.dto.UserDTO;
import com.itradingsolutions.itex.api.masters.industry.models.dto.IndustryDTO;
import com.itradingsolutions.itex.api.partners.clients.models.enums.ClientStatus;
import com.itradingsolutions.itex.api.partners.common.models.dto.PartnerDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class ClientDTO extends PartnerDTO<ClientStatus, ClientInfoDepDTO> {

    private String code;
    private IndustryDTO industry;
    private UserDTO changeProspectToClientBy;
    private ZonedDateTime changeProspectToClientAt;

    public String getShowName() {
        return "(%s) %s".formatted(this.getCode(), this.getName());
    }
}
