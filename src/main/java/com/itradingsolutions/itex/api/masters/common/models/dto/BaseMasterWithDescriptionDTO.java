package com.itradingsolutions.itex.api.masters.common.models.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class BaseMasterWithDescriptionDTO extends BaseMasterDTO {
    private String description;

    public void setDescription(String description) {
        this.description = normalizeText(description).trim();
    }
}
