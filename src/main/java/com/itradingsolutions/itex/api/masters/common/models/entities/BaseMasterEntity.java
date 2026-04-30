package com.itradingsolutions.itex.api.masters.common.models.entities;

import com.itradingsolutions.itex.api.common.models.entities.BaseEntity;
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
public abstract class BaseMasterEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -8322510321500992638L;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
