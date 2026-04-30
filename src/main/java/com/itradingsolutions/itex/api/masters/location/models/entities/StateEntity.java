package com.itradingsolutions.itex.api.masters.location.models.entities;

import com.itradingsolutions.itex.api.common.util.models.enums.UniqueDB;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

@Entity
@Table(name = "t_states", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "country_id"}, name = UniqueDB.STATE_NAME),
        @UniqueConstraint(columnNames = {"name_short", "country_id"}, name = UniqueDB.STATE_NAME_SHORT),
        @UniqueConstraint(columnNames = {"latitude", "longitude"}, name = UniqueDB.STATE_LATITUDE_LONGITUDE)
})
@Getter
@Setter
@ToString
public class StateEntity extends BaseLocationEntity {


    @Serial
    private static final long serialVersionUID = 3642815440604085631L;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private CountryEntity country;

    @Column(name = "name_short", nullable = false, length = 3)
    private String nameShort;

    @Column(name = "is_show_short_name", nullable = false)
    private boolean showShortName;
}
