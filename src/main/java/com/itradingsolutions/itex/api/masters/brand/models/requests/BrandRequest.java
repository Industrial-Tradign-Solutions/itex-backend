package com.itradingsolutions.itex.api.masters.brand.models.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class BrandRequest {

    private UUID id;

    @NotBlank(message = "{master.brand.name}")
    private String name;
}
