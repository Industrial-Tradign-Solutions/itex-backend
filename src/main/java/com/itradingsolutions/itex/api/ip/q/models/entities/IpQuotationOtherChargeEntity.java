package com.itradingsolutions.itex.api.ip.q.models.entities;

import com.itradingsolutions.itex.api.common.models.entities.BaseEntity;
import com.itradingsolutions.itex.api.common.util.models.enums.UniqueDB;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * Entity representing an additional charge (other charge) associated with an IP Quotation.
 * <p>
 * Other charges are additional fees or costs that are not directly related to products
 * but need to be included in the total quotation amount (e.g., shipping, handling, customs).
 * Each other charge must have a unique description per quotation.
 * </p>
 *
 * @see IpQuotationEntity
 */
@Entity
@Table(name = "t_ip_quotation_other_charges", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ip_q_id", "description"}, name = UniqueDB.IP_Q_OTHER_CHARGES_UNIQUE)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IpQuotationOtherChargeEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -2023051401234567890L;

    /**
     * The quotation to which this other charge belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ip_q_id", nullable = false)
    private IpQuotationEntity ipQuotation;

    /**
     * The monetary value of this charge.
     * <p>
     * Precision: 15 digits total, 2 decimal places.
     * Must be a non-negative value.
     * </p>
     */
    @Column(name = "value", nullable = false, precision = 15, scale = 2)
    private BigDecimal value = BigDecimal.ZERO;

    /**
     * Description of the charge (e.g., "Freight", "Customs Duty", "Handling Fee").
     * <p>
     * Must be unique within the same quotation.
     * Maximum length: 150 characters.
     * </p>
     */
    @Column(name = "description", nullable = false, length = 150)
    private String description;
}
