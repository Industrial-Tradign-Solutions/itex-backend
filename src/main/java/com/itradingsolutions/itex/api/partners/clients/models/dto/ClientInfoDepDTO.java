package com.itradingsolutions.itex.api.partners.clients.models.dto;

import com.itradingsolutions.itex.api.admin.user.models.dto.UserDTO;
import com.itradingsolutions.itex.api.partners.common.models.dto.PartnerInfoDepDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ClientInfoDepDTO extends PartnerInfoDepDTO<ClientContactDTO> {

    private UserDTO accountRep;
    private BigDecimal target;

}
