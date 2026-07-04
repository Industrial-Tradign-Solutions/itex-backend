package com.itradingsolutions.itex.api.ip.q.models.entities;

import com.itradingsolutions.itex.api.common.models.entities.BaseEntity;
import com.itradingsolutions.itex.api.common.util.models.enums.UniqueDB;
import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestOtherChargesEntity;
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

@Entity
@Table(name = "t_ip_quotation_other_charges_quote_request", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"qr_other_charge_id", "quotations_quote_request_id"}, name = UniqueDB.IP_Q_QR_OTHER_CHARGES_UNIQUE)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IpQuotationOtherChargesQuoteRequestEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -6075195655457424206L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotations_quote_request_id", nullable = false)
    private IpQuotationsQuoteRequestEntity quotationsQuoteRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qr_other_charge_id", nullable = false)
    private IpQuoteRequestOtherChargesEntity qrOtherCharge;
}
