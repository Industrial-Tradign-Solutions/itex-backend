package com.itradingsolutions.itex.api.masters.common.models.dto;

import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class BaseMasterDTO extends BaseDTO {

    private String name;
    private boolean active;

    public void setName(String name) {
        this.name = normalizeText(name.toUpperCase()).trim();
    }
}
