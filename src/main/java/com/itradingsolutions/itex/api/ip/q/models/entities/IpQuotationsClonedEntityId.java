package com.itradingsolutions.itex.api.ip.q.models.entities;

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
public class IpQuotationsClonedEntityId implements Serializable {

    @Serial
    private static final long serialVersionUID = -5578860940340918758L;

    @Column(name = "main_q_id", nullable = false)
    private UUID mainQId;

    @Column(name = "clone_q_id", nullable = false)
    private UUID cloneQId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        IpQuotationsClonedEntityId entity = (IpQuotationsClonedEntityId) o;
        return Objects.equals(this.mainQId, entity.mainQId) &&
                Objects.equals(this.cloneQId, entity.cloneQId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mainQId, cloneQId);
    }
}