package com.itradingsolutions.itex.api.common.salesconsecutive.models.entities;

import com.itradingsolutions.itex.api.common.salesconsecutive.models.enums.SalesConsecutiveType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * Composite key of {@link SalesConsecutiveFree}: the sequence type plus the released number.
 *
 * <p>The {@code type} discriminator keeps the released numbers of each sequence separate, so gaps of
 * one document type are never reused by another.</p>
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SalesConsecutiveFreeId implements Serializable {

    @Serial
    private static final long serialVersionUID = 7731905524198763012L;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 15)
    private SalesConsecutiveType type;

    @Column(name = "number", nullable = false)
    private Long number;
}
