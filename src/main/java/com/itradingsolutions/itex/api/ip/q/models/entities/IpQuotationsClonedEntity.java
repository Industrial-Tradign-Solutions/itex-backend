package com.itradingsolutions.itex.api.ip.q.models.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "t_ip_quotations_cloned")
@AllArgsConstructor
@NoArgsConstructor
public class IpQuotationsClonedEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -1456680831293465444L;

    @EmbeddedId
    private IpQuotationsClonedEntityId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("mainQId")
    @JoinColumn(name = "main_q_id")
    private IpQuotationEntity mainQuotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cloneQId")
    @JoinColumn(name = "clone_q_id")
    private IpQuotationEntity clonedQuotation;

    public void setId(UUID originalId, UUID clonedId) {
        this.id = new IpQuotationsClonedEntityId();
        this.id.setCloneQId(clonedId);
        this.id.setMainQId(originalId);
    }

    public void setId(IpQuotationsClonedEntityId id) {
        this.id = id;
    }
}