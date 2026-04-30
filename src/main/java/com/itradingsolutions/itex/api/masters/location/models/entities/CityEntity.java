package com.itradingsolutions.itex.api.masters.location.models.entities;

import com.itradingsolutions.itex.api.common.util.models.enums.UniqueDB;
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
@Table(name = "t_cities", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "state_id"}, name = UniqueDB.CITY_NAME),
        @UniqueConstraint(columnNames = {"latitude", "longitude"}, name = UniqueDB.CITY_LATITUDE_LONGITUDE)
})
@Getter
@Setter
@ToString
public class CityEntity extends BaseLocationEntity {

    @Serial
    private static final long serialVersionUID = -5124820347871703629L;

    @ManyToOne
    @JoinColumn(name = "state_id", nullable = false)
    private StateEntity state;

    public String getFullName() {
        return super.getName() + ", " + (this.state.isShowShortName() ? this.state.getNameShort() : this.state.getName()) + ", " + this.state.getCountry().getName();
    }
}
