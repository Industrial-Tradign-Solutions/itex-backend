package com.itradingsolutions.itex.api.masters.brand.models.entities;

import com.itradingsolutions.itex.api.common.models.entities.BaseEntity;
import com.itradingsolutions.itex.api.common.util.models.enums.UniqueDB;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

@Entity
@Table(name = "t_brands", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name", name = UniqueDB.BRAND_NAME)
})
@Getter
@Setter
@ToString
public class BrandEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -6770011054070302391L;

    @Column(name = "name", nullable = false, length = 1000, unique = true)
    private String name;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
