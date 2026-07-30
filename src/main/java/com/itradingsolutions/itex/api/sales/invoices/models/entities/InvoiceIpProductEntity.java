package com.itradingsolutions.itex.api.sales.invoices.models.entities;

import com.itradingsolutions.itex.api.common.models.entities.BaseEntity;
import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.ip.products.models.entity.IpProductEntity;
import com.itradingsolutions.itex.api.ip.products.models.enums.ProductCondition;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "t_invoice_ip_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceIpProductEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -3821449746510820304L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private InvoiceEntity invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private IpProductEntity product;

    @Column(name = "number", nullable = false)
    private Integer number;

    @Column(name = "quantity", nullable = false, precision = 15, scale = 5)
    private BigDecimal quantity;

    @Column(name = "unit_type", nullable = false, length = 50)
    private String unitType;

    @Column(name = "lead_time", nullable = false)
    private Integer leadTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "lead_time_type", nullable = false, length = 10)
    private LeadTime leadTimeType;

    @Column(name = "unit_price", nullable = false, precision = 15, scale = 5)
    private BigDecimal unitPrice;

    @Column(name = "profit_margin", nullable = false, precision = 3, scale = 2)
    private BigDecimal profitMargin;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition", nullable = false, length = 20)
    private ProductCondition condition;
}
