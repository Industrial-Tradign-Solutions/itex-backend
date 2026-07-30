package com.itradingsolutions.itex.api.sales.invoices.models.dto;

import com.itradingsolutions.itex.api.admin.user.models.dto.UserDTO;
import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentMethod;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class InvoicePaymentDTO extends BaseDTO {

    private BigDecimal amount = BigDecimal.ZERO;
    private LocalDate paymentDate;
    private PaymentMethod paymentMethod;
    private String receiptPath;
    private String receiptOriginalName;
    private String notes;
    private boolean voided;
    private String voidedReason;
    private ZonedDateTime voidedAt;
    private UserDTO voidedBy;
    private UserDTO registeredBy;

    public void setNotes(String notes) {
        if (notes != null)
            this.notes = notes.trim();
    }

    public void setVoidedById(UUID voidedById) {
        if (voidedById != null) {
            this.voidedBy = new UserDTO();
            this.voidedBy.setId(voidedById);
        }
    }

    public void setRegisteredById(UUID registeredById) {
        if (registeredById != null) {
            this.registeredBy = new UserDTO();
            this.registeredBy.setId(registeredById);
        }
    }
}
