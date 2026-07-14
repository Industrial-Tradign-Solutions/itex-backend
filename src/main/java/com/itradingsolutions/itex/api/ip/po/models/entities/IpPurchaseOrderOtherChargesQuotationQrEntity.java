package com.itradingsolutions.itex.api.ip.po.models.entities;

import com.itradingsolutions.itex.api.common.models.entities.BaseEntity;
import com.itradingsolutions.itex.api.common.util.models.enums.UniqueDB;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationOtherChargesQuoteRequestEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;

@Entity
@Table(name = "t_ip_purchase_order_other_charges_quotation_qr", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ip_po_id", "ip_q_qr_other_charge_id"}, name = UniqueDB.IP_PO_OTHER_CHARGES_Q_QR_UNIQUE)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderOtherChargesQuotationQrEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -6075195655457424208L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ip_po_id", nullable = false)
    private PurchaseOrderEntity purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ip_q_qr_other_charge_id", nullable = false)
    private IpQuotationOtherChargesQuoteRequestEntity quotationQrOtherCharge;
}
