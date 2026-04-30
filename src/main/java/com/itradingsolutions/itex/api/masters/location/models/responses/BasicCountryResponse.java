package com.itradingsolutions.itex.api.masters.location.models.responses;

import com.itradingsolutions.itex.api.masters.common.models.responses.BaseBasicMasterResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class BasicCountryResponse extends BaseBasicMasterResponse {

    private String nameShort;
}
