package com.itradingsolutions.itex.api.ip.po.models.entities;

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
public class PurchaseOrdersClonedEntityId implements Serializable {

    @Serial
    private static final long serialVersionUID = -5578860940340918759L;

    @Column(name = "main_po_id", nullable = false)
    private UUID mainPoId;

    @Column(name = "clone_po_id", nullable = false)
    private UUID clonePoId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        PurchaseOrdersClonedEntityId entity = (PurchaseOrdersClonedEntityId) o;
        return Objects.equals(this.mainPoId, entity.mainPoId) &&
                Objects.equals(this.clonePoId, entity.clonePoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mainPoId, clonePoId);
    }
}
