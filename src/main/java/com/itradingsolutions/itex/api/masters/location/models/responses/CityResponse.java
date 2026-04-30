package com.itradingsolutions.itex.api.masters.location.models.responses;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class CityResponse extends BaseLocationResponse {

    private StateResponse state;
    private String fullName;
}
