package com.itradingsolutions.itex.api.masters.location.models.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class CountryRequest extends BaseLocationRequest {

    @NotBlank(message = "The short name field cannot be empty")
    private String nameShort;
}
