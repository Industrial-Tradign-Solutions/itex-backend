package com.itradingsolutions.itex.api.ip.qr.models.entities;

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
public class IpQuoteRequestsClonedEntityId implements Serializable {

    @Serial
    private static final long serialVersionUID = -5578860940340918757L;

    @Column(name = "main_qr_id", nullable = false)
    private UUID mainQrId;

    @Column(name = "clone_qr_id", nullable = false)
    private UUID cloneQrId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        IpQuoteRequestsClonedEntityId entity = (IpQuoteRequestsClonedEntityId) o;
        return Objects.equals(this.mainQrId, entity.mainQrId) &&
                Objects.equals(this.cloneQrId, entity.cloneQrId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mainQrId, cloneQrId);
    }

}
