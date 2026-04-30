package com.itradingsolutions.itex.api.partners.suppliers.models.entities;

import com.itradingsolutions.itex.api.common.util.models.enums.UniqueDB;
import com.itradingsolutions.itex.api.partners.common.models.entities.PartnerEntity;
import com.itradingsolutions.itex.api.partners.suppliers.models.enums.SupplierStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.List;

@Entity
@Table(name = "t_suppliers", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tax_id"}, name = UniqueDB.SUPPLIER_TAX_ID)
})
@Getter
@Setter
public class SupplierEntity extends PartnerEntity {
    
    @Serial
    private static final long serialVersionUID = -4878272712229803672L;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 25)
    private SupplierStatus status;

    @Column(name = "wire_ach_instructions", columnDefinition = "TEXT")
    private String wireAchInstructions;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "supplier", cascade =  CascadeType.ALL)
    private List<SupplierInfoDepEntity> infoByDepartment;
}
