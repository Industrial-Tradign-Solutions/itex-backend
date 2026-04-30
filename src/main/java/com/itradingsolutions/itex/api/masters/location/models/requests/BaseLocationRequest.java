package com.itradingsolutions.itex.api.masters.location.models.requests;

import com.itradingsolutions.itex.api.masters.common.models.requests.BaseMasterRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class BaseLocationRequest extends BaseMasterRequest {

    @NotBlank(message = "The Longitude field cannot be empty")
    @Pattern(regexp = "^-?[0-9]*\\.?[0-9]+$", message = "The longitude does not comply with the specified scheme")
    private String longitude;

    @NotBlank(message = "The Latitude field cannot be empty")
    @Pattern(regexp = "^-?[0-9]*\\.?[0-9]+$", message = "The latitude does not comply with the specified scheme")
    private String latitude;
}
