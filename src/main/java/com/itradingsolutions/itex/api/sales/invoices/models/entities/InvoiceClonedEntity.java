package com.itradingsolutions.itex.api.sales.invoices.models.entities;

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
@Table(name = "t_invoice_cloned", uniqueConstraints = {
        @UniqueConstraint(columnNames = "clone_invoice_id", name = UniqueDB.INVOICE_CLONED_ID)
})
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceClonedEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -1456680831293465502L;

    @EmbeddedId
    private InvoiceClonedEntityId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("mainInvoiceId")
    @JoinColumn(name = "main_invoice_id")
    private InvoiceEntity mainInvoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cloneInvoiceId")
    @JoinColumn(name = "clone_invoice_id")
    private InvoiceEntity clonedInvoice;

    public void setId(UUID mainInvoiceId, UUID cloneInvoiceId) {
        this.id = new InvoiceClonedEntityId();
        this.id.setMainInvoiceId(mainInvoiceId);
        this.id.setCloneInvoiceId(cloneInvoiceId);
    }

    public void setId(InvoiceClonedEntityId id) {
        this.id = id;
    }
}
