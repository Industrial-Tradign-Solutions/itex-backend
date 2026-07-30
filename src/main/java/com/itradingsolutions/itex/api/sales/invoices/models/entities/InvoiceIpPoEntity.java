package com.itradingsolutions.itex.api.sales.invoices.models.entities;

import com.itradingsolutions.itex.api.ip.po.models.entities.IpPurchaseOrderEntity;
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
@Table(name = "t_invoice_ip_po")
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceIpPoEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -1456680831293465501L;

    @EmbeddedId
    private InvoiceIpPoEntityId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("invoiceId")
    @JoinColumn(name = "invoice_id")
    private InvoiceEntity invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ipPoId")
    @JoinColumn(name = "ip_po_id")
    private IpPurchaseOrderEntity purchaseOrder;

    public void setId(UUID invoiceId, UUID ipPoId) {
        this.id = new InvoiceIpPoEntityId();
        this.id.setInvoiceId(invoiceId);
        this.id.setIpPoId(ipPoId);
    }
}
