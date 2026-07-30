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
public class InvoiceIpPoEntityId implements Serializable {

    @Serial
    private static final long serialVersionUID = -5578860940340918801L;

    @Column(name = "invoice_id", nullable = false)
    private UUID invoiceId;

    @Column(name = "ip_po_id", nullable = false)
    private UUID ipPoId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        InvoiceIpPoEntityId entity = (InvoiceIpPoEntityId) o;
        return Objects.equals(this.invoiceId, entity.invoiceId) &&
                Objects.equals(this.ipPoId, entity.ipPoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(invoiceId, ipPoId);
    }
}
