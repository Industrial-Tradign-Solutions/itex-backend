package com.itradingsolutions.itex.api.masters.location.models.responses;

import com.itradingsolutions.itex.api.masters.common.models.responses.BaseBasicMasterResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseLocationResponse extends BaseBasicMasterResponse {

    private String longitude;
    private String latitude;
}
