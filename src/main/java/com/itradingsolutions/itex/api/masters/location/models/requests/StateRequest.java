package com.itradingsolutions.itex.api.masters.location.models.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class StateRequest extends BaseLocationRequest {

    @NotNull(message = "The country field cannot be empty")
    private UUID countryId;

    @NotBlank(message = "The short name field cannot be empty")
    private String nameShort;

    private boolean showShortName;
}
