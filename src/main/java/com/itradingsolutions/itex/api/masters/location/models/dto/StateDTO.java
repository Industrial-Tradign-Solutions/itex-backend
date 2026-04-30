package com.itradingsolutions.itex.api.masters.location.models.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class StateDTO extends BaseLocationDTO {

    private CountryDTO country;
    private String nameShort;
    private boolean showShortName;

    public void setCountryId(UUID countryId) {
        this.country = new CountryDTO();
        this.country.setId(countryId);
    }
}
