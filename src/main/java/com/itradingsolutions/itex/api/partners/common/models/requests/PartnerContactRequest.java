package com.itradingsolutions.itex.api.partners.common.models.requests;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public abstract class PartnerContactRequest<P extends PartnerContactPhoneRequest> {

    private UUID id;

    @NotBlank(message = "The Name field is required")
    private String name;

    private String title;

    @Email(message = "The E-mail not is valid")
    private String email;

    @NotNull(message = "The Is Main field is required")
    private boolean validMain;

    @NotNull(message = "The active field is required")
    private boolean active;

    @Valid
    @Size(min = 1, message = "You must enter at least 1 phone number")
    private List<@Valid P> listPhones;
}
