package com.itradingsolutions.itex.api.sales.invoices.models.entities;

import com.itradingsolutions.itex.api.common.models.entities.BaseEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceChargeType;
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
@Table(name = "t_invoice_charges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceChargeEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -3821449746510820301L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private InvoiceEntity invoice;

    @Column(name = "description", nullable = false, length = 100)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 100)
    private InvoiceChargeType type;

    @Column(name = "value", nullable = false, precision = 15, scale = 5)
    private BigDecimal value;
}
