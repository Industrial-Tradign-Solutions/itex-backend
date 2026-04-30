package com.itradingsolutions.itex.api.masters.common.models.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class BaseMasterWithDescriptionRequest extends BaseMasterRequest {

    private String description;
}
