package com.itradingsolutions.itex.api.partners.common.models.entities;

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
public abstract class PartnerContactEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 6459449299296390005L;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "is_main", nullable = false)
    private boolean validMain;

}
