package com.itradingsolutions.itex.api.masters.location.models.dto;


import com.itradingsolutions.itex.api.masters.common.models.dto.BaseMasterDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class BaseLocationDTO extends BaseMasterDTO {
    private String latitude;
    private String longitude;
}
