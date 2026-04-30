package com.itradingsolutions.itex.api.partners.common.models.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public abstract class PartnerInfoDepRequest<C extends PartnerContactRequest<?>> {

    private UUID id;

    @NotNull(message = "The Department field is required")
    private UUID departmentId;

    private String notes;

    @Valid
    private List<@Valid C> listContacts;
}
