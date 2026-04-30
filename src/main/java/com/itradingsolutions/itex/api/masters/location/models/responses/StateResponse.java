package com.itradingsolutions.itex.api.masters.location.models.responses;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class StateResponse extends BaseLocationResponse {

    private CountryResponse country;
    private String nameShort;
    private boolean showShortName;

}
