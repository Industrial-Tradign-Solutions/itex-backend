package com.itradingsolutions.itex.api.partners.suppliers.models.entities;

import com.itradingsolutions.itex.api.common.util.models.enums.UniqueDB;
import com.itradingsolutions.itex.api.partners.common.models.entities.PartnerInfoByDepEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "t_suppliers_info_by_department", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"supplier_id", "department_id"}, name = UniqueDB.SUPPLIER_DEPARTMENT_INFO),
})
@Getter
@Setter
public class SupplierInfoDepEntity extends PartnerInfoByDepEntity {

    @Serial
    private static final long serialVersionUID = -8775039616349870664L;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private SupplierEntity supplier;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "supplierInfoDep", cascade = CascadeType.ALL)
    private List<SupplierContactEntity> listContacts;

    public List<SupplierContactEntity> getListContacts() {
        if (listContacts == null || listContacts.isEmpty())
            return new ArrayList<>();

        listContacts.sort(Comparator.comparing(SupplierContactEntity::isValidMain).reversed());
        return listContacts;
    }
}
