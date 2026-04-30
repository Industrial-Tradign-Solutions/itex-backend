package com.itradingsolutions.itex.api.masters.location.models.entities;

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
@Table(name = "t_countries", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name", name = UniqueDB.COUNTRY_NAME),
        @UniqueConstraint(columnNames = "name_short", name = UniqueDB.COUNTRY_NAME_SHORT),
        @UniqueConstraint(columnNames = {"latitude", "longitude"}, name = UniqueDB.COUNTRY_LATITUDE_LONGITUDE)
})
@Getter
@Setter
@ToString
public class CountryEntity extends BaseLocationEntity {


    @Serial
    private static final long serialVersionUID = 6833769123517261160L;

    @Column(name = "name", nullable = false, length = 70)
    private String name;

    @Column(name = "name_short", nullable = false, length = 3)
    private String nameShort;
}
