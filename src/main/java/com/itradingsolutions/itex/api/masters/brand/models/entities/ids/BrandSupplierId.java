package com.itradingsolutions.itex.api.masters.brand.models.entities.ids;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Embeddable
public class BrandSupplierId implements Serializable {

    @Serial
    private static final long serialVersionUID = -7873965373850014364L;

    @Column(name = "supplier_id")
    private UUID supplierId;

    @Column(name = "brand_id")
    private UUID brandId;
}
