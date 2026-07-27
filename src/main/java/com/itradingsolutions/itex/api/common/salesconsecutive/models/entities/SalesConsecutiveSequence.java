package com.itradingsolutions.itex.api.common.salesconsecutive.models.entities;

import com.itradingsolutions.itex.api.common.salesconsecutive.models.enums.SalesConsecutiveType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * High-water counter for a sales consecutive sequence (one row per {@link SalesConsecutiveType}).
 *
 * <p>{@code currentValue} holds the last number assigned for the type. The next number is derived as
 * {@code currentValue + 1} (unless a released gap is reused). Allocation is serialized with a
 * pessimistic write lock on this row.</p>
 */
@Entity
@Table(name = "t_sales_consecutive_sequence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SalesConsecutiveSequence implements Serializable {

    @Serial
    private static final long serialVersionUID = -8412330761923845110L;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 15)
    private SalesConsecutiveType type;

    @Column(name = "current_value", nullable = false)
    private Long currentValue;
}
