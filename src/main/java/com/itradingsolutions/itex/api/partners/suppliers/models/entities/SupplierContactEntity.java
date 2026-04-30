package com.itradingsolutions.itex.api.partners.suppliers.models.entities;

import com.itradingsolutions.itex.api.partners.common.models.entities.PartnerContactEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "t_suppliers_contacts")
@Getter
@Setter
public class SupplierContactEntity extends PartnerContactEntity {

    @Serial
    private static final long serialVersionUID = 8382379568305591039L;

    @ManyToOne
    @JoinColumn(name = "supplier_info_dep_id", nullable = false)
    private SupplierInfoDepEntity supplierInfoDep;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "supplierContact", cascade = CascadeType.ALL)
    private List<SupplierContactPhoneEntity> listPhones;

    public List<SupplierContactPhoneEntity> getListPhones() {
        if (listPhones == null || listPhones.isEmpty())
            return new ArrayList<>();
        listPhones.sort(Comparator.comparing(SupplierContactPhoneEntity::isValidMain).reversed());
        return listPhones;
    }

    public String getMainPhone() {
        return listPhones == null || listPhones.isEmpty()
                ? ""
                : listPhones.stream()
                .filter(SupplierContactPhoneEntity::isValidMain)
                .map(SupplierContactPhoneEntity::getFullPhone)
                .findFirst()
                .orElse("");
    }

}
