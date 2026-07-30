package com.itradingsolutions.itex.api.sales.invoices.models.entities;

import com.itradingsolutions.itex.api.common.models.entities.BaseEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceTaxType;
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
@Table(name = "t_invoice_taxes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceTaxEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -3821449746510820302L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private InvoiceEntity invoice;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private InvoiceTaxType type;

    @Column(name = "description", nullable = false, length = 100)
    private String description;

    @Column(name = "rate", nullable = false, precision = 5, scale = 4)
    private BigDecimal rate;

    @Column(name = "taxable_base", nullable = false, precision = 15, scale = 5)
    private BigDecimal taxableBase;

    @Column(name = "value", nullable = false, precision = 15, scale = 5)
    private BigDecimal value;
}
