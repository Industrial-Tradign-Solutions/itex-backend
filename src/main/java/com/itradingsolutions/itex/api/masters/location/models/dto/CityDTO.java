package com.itradingsolutions.itex.api.masters.location.models.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class CityDTO extends BaseLocationDTO {

    private StateDTO state;
    private String fullName;

    public void setStateId(UUID stateId) {
        this.state = new StateDTO();
        this.state.setId(stateId);
    }
}
