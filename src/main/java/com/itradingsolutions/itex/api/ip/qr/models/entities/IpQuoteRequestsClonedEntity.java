package com.itradingsolutions.itex.api.ip.qr.models.entities;

import com.itradingsolutions.itex.api.common.util.models.enums.UniqueDB;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "t_ip_quote_requests_cloned", uniqueConstraints = {
        @UniqueConstraint(columnNames = "clone_qr_id", name = UniqueDB.IP_QR_CLONED_ID)
})
@AllArgsConstructor
@NoArgsConstructor
public class IpQuoteRequestsClonedEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -1456680831293465443L;

    @EmbeddedId
    private IpQuoteRequestsClonedEntityId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("mainQrId")
    @JoinColumn(name = "main_qr_id")
    private IpQuoteRequestEntity mainQr;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cloneQrId")
    @JoinColumn(name = "clone_qr_id")
    private IpQuoteRequestEntity clonedQr;

    public void setId(UUID originalId, UUID clonedId) {
        this.id = new IpQuoteRequestsClonedEntityId();
        this.id.setCloneQrId(clonedId);
        this.id.setMainQrId(originalId);
    }

    public void setId(IpQuoteRequestsClonedEntityId id) {
        this.id = id;
    }
}
