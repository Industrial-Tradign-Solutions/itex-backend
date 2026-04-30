package com.itradingsolutions.itex.api.masters.industry.models.entities;

import com.itradingsolutions.itex.api.common.util.models.enums.UniqueDB;
import com.itradingsolutions.itex.api.masters.common.models.entities.BaseMasterWithDescriptionEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

@Entity
@Table(name = "t_industries", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name", name = UniqueDB.INDUSTRY_NAME)
})
@Getter
@Setter
@ToString
public class IndustryEntity extends BaseMasterWithDescriptionEntity {

    @Serial
    private static final long serialVersionUID = 71746595030118599L;
}
