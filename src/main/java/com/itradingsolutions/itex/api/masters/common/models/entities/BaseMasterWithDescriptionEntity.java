package com.itradingsolutions.itex.api.masters.common.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

@Getter
@Setter
@ToString
@MappedSuperclass
public abstract class BaseMasterWithDescriptionEntity extends BaseMasterEntity {

    @Serial
    private static final long serialVersionUID = 2235534012042620213L;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
