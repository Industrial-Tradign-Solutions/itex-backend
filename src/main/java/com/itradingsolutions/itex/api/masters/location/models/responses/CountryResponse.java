package com.itradingsolutions.itex.api.masters.location.models.responses;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@SuperBuilder
public class CountryResponse extends BaseLocationResponse {

    private String nameShort;

}
