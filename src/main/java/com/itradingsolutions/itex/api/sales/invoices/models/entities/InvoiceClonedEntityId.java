package com.itradingsolutions.itex.api.sales.invoices.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Embeddable
public class InvoiceClonedEntityId implements Serializable {

    @Serial
    private static final long serialVersionUID = -5578860940340918802L;

    @Column(name = "main_invoice_id", nullable = false)
    private UUID mainInvoiceId;

    @Column(name = "clone_invoice_id", nullable = false)
    private UUID cloneInvoiceId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        InvoiceClonedEntityId entity = (InvoiceClonedEntityId) o;
        return Objects.equals(this.mainInvoiceId, entity.mainInvoiceId) &&
                Objects.equals(this.cloneInvoiceId, entity.cloneInvoiceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mainInvoiceId, cloneInvoiceId);
    }
}
