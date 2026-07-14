package com.itradingsolutions.itex.api.ip.po.models.entities;

import com.itradingsolutions.itex.api.common.models.entities.BaseEntity;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationProductEntity;
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

@Entity
@Table(name = "t_ip_purchase_order_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IpPurchaseOrderProductEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 4785147161350122007L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ip_po_id", nullable = false)
    private IpPurchaseOrderEntity purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotation_product_id")
    private IpQuotationProductEntity quotationProduct;

    @Column(name = "number", nullable = false)
    private Integer number;
}
