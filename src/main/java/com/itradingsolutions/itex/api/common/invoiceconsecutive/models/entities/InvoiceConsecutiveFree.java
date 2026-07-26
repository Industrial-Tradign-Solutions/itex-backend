package com.itradingsolutions.itex.api.common.invoiceconsecutive.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * A released DRAFT invoice number available for reuse.
 *
 * <p>When a draft invoice is deleted its number is inserted here. The draft allocator always reuses
 * the lowest available number ({@code MIN(number)}) before growing the counter, guaranteeing that no
 * number is ever skipped. FINAL numbers are never released (issued invoices cannot be deleted), so
 * this table only ever holds DRAFT numbers.</p>
 */
@Entity
@Table(name = "t_invoice_consecutive_free")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceConsecutiveFree implements Serializable {

    @Serial
    private static final long serialVersionUID = 5327841190236745882L;

    @Id
    @Column(name = "number", nullable = false, updatable = false)
    private Long number;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;
}
