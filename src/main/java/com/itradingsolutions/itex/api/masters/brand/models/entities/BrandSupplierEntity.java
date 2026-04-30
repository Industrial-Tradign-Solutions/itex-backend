package com.itradingsolutions.itex.api.masters.brand.models.entities;

import com.itradingsolutions.itex.api.masters.brand.models.entities.ids.BrandSupplierId;
import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierEntity;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "t_brands_suppliers")
@Getter
@Setter
public class BrandSupplierEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 628004264118406042L;

    @EmbeddedId
    private BrandSupplierId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("supplierId")
    @JoinColumn(name = "supplier_id", nullable = false)
    private SupplierEntity supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("brandId")
    @JoinColumn(name = "brand_id", nullable = false)
    private BrandEntity brand;
}
