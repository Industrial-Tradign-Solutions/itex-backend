package com.itradingsolutions.itex.api.ip.po.models.entities;

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
@Table(name = "t_ip_purchase_orders_cloned")
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrdersClonedEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -1456680831293465445L;

    @EmbeddedId
    private PurchaseOrdersClonedEntityId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("mainPoId")
    @JoinColumn(name = "main_po_id")
    private PurchaseOrderEntity mainPurchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("clonePoId")
    @JoinColumn(name = "clone_po_id")
    private PurchaseOrderEntity clonedPurchaseOrder;

    public void setId(UUID originalId, UUID clonedId) {
        this.id = new PurchaseOrdersClonedEntityId();
        this.id.setClonePoId(clonedId);
        this.id.setMainPoId(originalId);
    }

    public void setId(PurchaseOrdersClonedEntityId id) {
        this.id = id;
    }
}
