package com.itradingsolutions.itex.api.partners.suppliers.models.entities;

import com.itradingsolutions.itex.api.partners.common.models.entities.PartnerContactPhoneEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Entity
@Table(name = "t_suppliers_contacts_phones")
@Getter
@Setter
public class SupplierContactPhoneEntity extends PartnerContactPhoneEntity {

    @Serial
    private static final long serialVersionUID = -234974254003015956L;

    @ManyToOne
    @JoinColumn(name = "supplier_contact_id", nullable = false)
    private SupplierContactEntity supplierContact;

}
