package com.itradingsolutions.itex.api.sales.invoices.models.entities;

import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import com.itradingsolutions.itex.api.common.models.entities.BaseEntity;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.common.util.models.enums.Incoterms;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;
import com.itradingsolutions.itex.api.common.util.models.enums.UniqueDB;
import com.itradingsolutions.itex.api.masters.location.models.entities.CityEntity;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientContactEntity;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "t_invoices", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"draft_number"}, name = UniqueDB.INVOICE_DRAFT_NUMBER),
        @UniqueConstraint(columnNames = {"number"}, name = UniqueDB.INVOICE_NUMBER)
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -3821449746510820201L;

    @Column(name = "draft_number", nullable = false, unique = true)
    private Long draftNumber;

    @Column(name = "number", unique = true)
    private Long number;

    @Column(name = "department", nullable = false, length = 3)
    private String department;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private InvoiceStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, length = 20)
    private Currency currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_contact_id")
    private ClientContactEntity clientContact;

    @Column(name = "ship_to_name", nullable = false, length = 300)
    private String shipToName;

    @Column(name = "ship_to_address", nullable = false, length = 500)
    private String shipToAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ship_to_city", nullable = false)
    private CityEntity shipToCity;

    @Column(name = "ship_to_phone", nullable = false, length = 20)
    private String shipToPhone;

    @Column(name = "ship_to_contact_name", nullable = false, length = 50)
    private String shipToContactName;

    @Column(name = "ship_to_email", nullable = false, length = 100)
    private String shipToEmail;

    @Column(name = "order_number", length = 100)
    private String orderNumber;

    @Column(name = "via", length = 10)
    private String via;

    @Enumerated(EnumType.STRING)
    @Column(name = "incoterms", nullable = false, length = 20)
    private Incoterms incoterms;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_terms", length = 40)
    private PaymentTerms paymentTerms;

    @Column(name = "awb_bl", length = 100)
    private String awbBl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_rep_id", nullable = false)
    private UserEntity salesRep;

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "internal_remarks", columnDefinition = "TEXT")
    private String internalRemarks;

    @Column(name = "packing_list", length = 100)
    private String packingList;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 5)
    private BigDecimal totalAmount;

    @Column(name = "paid_amount", nullable = false, precision = 15, scale = 5)
    private BigDecimal paidAmount;

    @Column(name = "due_at")
    private ZonedDateTime dueAt;

    @Column(name = "is_overdue", nullable = false)
    private boolean overdue;

    @Column(name = "overdue_notified_at")
    private ZonedDateTime overdueNotifiedAt;

    @Column(name = "issued_at")
    private ZonedDateTime issuedAt;

    @Column(name = "partial_paid_at")
    private ZonedDateTime partialPaidAt;

    @Column(name = "paid_at")
    private ZonedDateTime paidAt;

    @Column(name = "cancelled_at")
    private ZonedDateTime cancelledAt;

    @Column(name = "cancel_reason", columnDefinition = "TEXT")
    private String cancelReason;

    @Column(name = "path_pdf", length = 1000)
    private String pdfUrl;

    @Column(name = "open_at")
    private ZonedDateTime openAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "open_by_user_id")
    private UserEntity openBy;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceChargeEntity> charges;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceTaxEntity> taxes;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoicePaymentEntity> payments;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceIpProductEntity> products;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceIpPoEntity> linkedPurchaseOrders;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "mainInvoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceClonedEntity> clonedInvoices;
}
