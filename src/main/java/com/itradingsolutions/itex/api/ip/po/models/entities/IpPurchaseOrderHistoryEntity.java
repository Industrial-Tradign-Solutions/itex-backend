package com.itradingsolutions.itex.api.ip.po.models.entities;

import com.itradingsolutions.itex.api.common.models.entities.HistoryEntity;
import com.itradingsolutions.itex.api.ip.po.models.enums.PurchaseOrderHistoryAction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.util.UUID;

@Entity
@Table(name = "t_ip_purchase_orders_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderHistoryEntity extends HistoryEntity {

    @Serial
    private static final long serialVersionUID = 9087654321234567891L;

    @Column(name = "ip_po_id", nullable = false)
    private UUID ipPurchaseOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 50)
    private PurchaseOrderHistoryAction action;
}
