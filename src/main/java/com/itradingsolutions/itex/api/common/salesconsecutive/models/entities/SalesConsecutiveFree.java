package com.itradingsolutions.itex.api.common.salesconsecutive.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * A released number available for reuse, per sequence type.
 *
 * <p>When a draft document is deleted its number is inserted here. The allocator always reuses the
 * lowest available number for that type ({@code MIN(number)}) before growing the counter, guaranteeing
 * that no number is ever skipped. Final numbers (INV/MEMO) are never released, so for those types this
 * table stays empty.</p>
 */
@Entity
@Table(name = "t_sales_consecutive_free")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalesConsecutiveFree implements Serializable {

    @Serial
    private static final long serialVersionUID = 5327841190236745882L;

    @EmbeddedId
    private SalesConsecutiveFreeId id;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;
}
