package com.itradingsolutions.itex.api.masters.common.models.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class BaseMasterRequest {

    @NotBlank(message = "The name field cannot be empty")
    private String name;
}
