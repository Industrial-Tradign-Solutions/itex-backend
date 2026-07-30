package com.itradingsolutions.itex.api.sales.invoices.models.entities;

import com.itradingsolutions.itex.api.common.models.entities.HistoryEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceHistoryAction;
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
@Table(name = "t_invoice_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceHistoryEntity extends HistoryEntity {

    @Serial
    private static final long serialVersionUID = 9087654321234567901L;

    @Column(name = "invoice_id", nullable = false)
    private UUID invoice;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 50)
    private InvoiceHistoryAction action;
}
