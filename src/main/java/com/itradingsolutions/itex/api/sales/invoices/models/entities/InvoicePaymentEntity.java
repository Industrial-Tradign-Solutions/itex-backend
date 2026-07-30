package com.itradingsolutions.itex.api.sales.invoices.models.entities;

import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import com.itradingsolutions.itex.api.common.models.entities.BaseEntity;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentMethod;
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
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Entity
@Table(name = "t_invoice_payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoicePaymentEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -3821449746510820303L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private InvoiceEntity invoice;

    @Column(name = "amount", nullable = false, precision = 15, scale = 5)
    private BigDecimal amount;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 40)
    private PaymentMethod paymentMethod;

    @Column(name = "receipt_path", nullable = false, length = 1000)
    private String receiptPath;

    @Column(name = "receipt_original_name", nullable = false, length = 255)
    private String receiptOriginalName;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_voided", nullable = false)
    private boolean voided;

    @Column(name = "voided_reason", columnDefinition = "TEXT")
    private String voidedReason;

    @Column(name = "voided_at")
    private ZonedDateTime voidedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voided_by_user_id")
    private UserEntity voidedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registered_by_user_id", nullable = false)
    private UserEntity registeredBy;
}
