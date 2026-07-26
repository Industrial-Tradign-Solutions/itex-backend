package com.itradingsolutions.itex.api.ip.po.models.entities;

import com.itradingsolutions.itex.api.common.models.entities.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.math.BigDecimal;

@Entity
@Table(name = "t_ip_purchase_orders_other_charges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IpPurchaseOrderOtherChargeEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -2023051401234567891L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ip_po_id", nullable = false)
    private IpPurchaseOrderEntity purchaseOrder;

    @Column(name = "value", nullable = false, precision = 15, scale = 2)
    private BigDecimal value;

    @Column(name = "description", nullable = false, length = 150)
    private String description;
}
