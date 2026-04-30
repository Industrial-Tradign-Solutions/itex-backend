package com.itradingsolutions.itex.api.partners.clients.models.requests;


import com.itradingsolutions.itex.api.partners.common.models.requests.PartnerInfoDepRequest;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class ClientInfoDepRequest extends PartnerInfoDepRequest<ClientContactRequest> {

    private UUID accountRepId;

    @NotNull(message = "The target is required")
    @DecimalMin(value = "0.00",  message = "The minimum value of the target must be 0.000")
    private BigDecimal target;


}
