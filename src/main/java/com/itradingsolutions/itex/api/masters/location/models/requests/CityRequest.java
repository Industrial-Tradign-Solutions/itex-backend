package com.itradingsolutions.itex.api.masters.location.models.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class CityRequest  extends BaseLocationRequest {

    @NotNull(message = "The state field cannot be empty")
    private UUID stateId;
}
