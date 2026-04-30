package com.itradingsolutions.itex.api.partners.clients.models.requests;


import com.itradingsolutions.itex.api.partners.clients.models.enums.ClientStatus;
import com.itradingsolutions.itex.api.partners.common.models.requests.PartnerRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ClientRequest extends PartnerRequest<ClientStatus, ClientInfoDepRequest> {

    @NotBlank(message = "The Code field is required")
    private String code;

    private UUID industryId;

}
