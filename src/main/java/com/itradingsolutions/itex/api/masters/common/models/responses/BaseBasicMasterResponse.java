package com.itradingsolutions.itex.api.masters.common.models.responses;

import com.itradingsolutions.itex.api.common.models.responses.BaseResponse;
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
public abstract class BaseBasicMasterResponse extends BaseResponse {

    private String name;
    private boolean active;
}
