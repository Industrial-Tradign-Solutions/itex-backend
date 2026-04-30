package com.itradingsolutions.itex.api.masters.location.models.entities;

import com.itradingsolutions.itex.api.masters.common.models.entities.BaseMasterEntity;
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
public abstract class BaseLocationEntity extends BaseMasterEntity {

    @Serial
    private static final long serialVersionUID = -6460724757645032919L;

    @Column(name = "latitude", nullable = false)
    private String latitude;

    @Column(name = "longitude", nullable = false)
    private String longitude;
}
