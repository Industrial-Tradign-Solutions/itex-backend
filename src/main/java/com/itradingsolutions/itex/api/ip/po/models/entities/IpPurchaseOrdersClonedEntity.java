package com.itradingsolutions.itex.api.ip.po.models.entities;

import com.itradingsolutions.itex.api.common.util.models.enums.UniqueDB;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(name = "t_ip_purchase_orders_cloned", uniqueConstraints = {
        @UniqueConstraint(columnNames = "clone_po_id", name = UniqueDB.IP_PO_CLONED_ID)
})
@AllArgsConstructor
@NoArgsConstructor
public class IpPurchaseOrdersClonedEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -1456680831293465445L;

    @EmbeddedId
    private IpPurchaseOrdersClonedEntityId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("mainPoId")
    @JoinColumn(name = "main_po_id")
    private IpPurchaseOrderEntity mainPurchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("clonePoId")
    @JoinColumn(name = "clone_po_id")
    private IpPurchaseOrderEntity clonedPurchaseOrder;

    public void setId(UUID originalId, UUID clonedId) {
        this.id = new IpPurchaseOrdersClonedEntityId();
        this.id.setClonePoId(clonedId);
        this.id.setMainPoId(originalId);
    }

    public void setId(IpPurchaseOrdersClonedEntityId id) {
        this.id = id;
    }
}
