package com.itradingsolutions.itex.api.sales.invoices.models.dto;

import com.itradingsolutions.itex.api.admin.user.models.dto.UserDTO;
import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveDepartment;
import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.common.util.models.enums.Incoterms;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;
import com.itradingsolutions.itex.api.masters.location.models.dto.CityDTO;
import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientContactDTO;
import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.InvoiceMoney;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceStatus;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceVia;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class InvoiceDTO extends BaseDTO {

    private Long draftNumber;
    private Long number;
    private ConsecutiveDepartment department;
    private InvoiceStatus status;
    private Currency currency;
    private ClientDTO client;
    private ClientContactDTO clientContact;
    private String shipToName;
    private String shipToAddress;
    private CityDTO shipToCity;
    private String shipToPhone;
    private String shipToContactName;
    private String shipToEmail;
    private String orderNumber;
    private InvoiceVia via;
    private Incoterms incoterms;
    private PaymentTerms paymentTerms;
    private String awbBl;
    private UserDTO salesRep;
    private String remarks;
    private String internalRemarks;
    private String packingList;
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private BigDecimal paidAmount = BigDecimal.ZERO;
    private ZonedDateTime dueAt;
    private boolean overdue;
    private ZonedDateTime overdueNotifiedAt;
    private ZonedDateTime issuedAt;
    private ZonedDateTime partialPaidAt;
    private ZonedDateTime paidAt;
    private ZonedDateTime cancelledAt;
    private String cancelReason;
    private String pdfUrl;
    private ZonedDateTime openAt;
    private UserDTO openBy;

    private List<InvoiceProductDTO> products;
    private List<InvoiceChargeDTO> charges;
    private List<InvoiceTaxDTO> taxes;
    private List<InvoicePurchaseOrderDTO> linkedPurchaseOrders;
    private List<InvoiceDTO> clonedInvoices;
    private InvoiceDTO clonedByInvoice;

    public BigDecimal getProductsTotal() {
        return InvoiceMoney.scaled(Optional.ofNullable(products).orElseGet(Collections::emptyList).stream()
                .map(InvoiceProductDTO::getSellingExtendedPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    public BigDecimal getChargesTotal() {
        return InvoiceMoney.scaled(Optional.ofNullable(charges).orElseGet(Collections::emptyList).stream()
                .map(InvoiceChargeDTO::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    public BigDecimal getTaxesTotal() {
        return InvoiceMoney.scaled(Optional.ofNullable(taxes).orElseGet(Collections::emptyList).stream()
                .map(InvoiceTaxDTO::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    public String getName() {
        return number != null ? number.toString() : draftNumber != null ? draftNumber.toString() : null;
    }

    /**
     * Whether the invoice was settled after its due date. Derived rather than stored: {@code dueAt}
     * and {@code paidAt} both survive the payment, so the fact is already in the row — {@code
     * isOverdue} is cleared when the money arrives and cannot answer this later.
     */
    public boolean isPaidLate() {
        return paidAt != null && dueAt != null && paidAt.isAfter(dueAt);
    }

    public void setShipToName(String shipToName) {
        if (shipToName != null)
            this.shipToName = shipToName.trim();
    }

    public void setShipToAddress(String shipToAddress) {
        if (shipToAddress != null)
            this.shipToAddress = shipToAddress.trim();
    }

    public void setShipToPhone(String shipToPhone) {
        if (shipToPhone != null)
            this.shipToPhone = shipToPhone.trim();
    }

    public void setShipToContactName(String shipToContactName) {
        if (shipToContactName != null)
            this.shipToContactName = shipToContactName.trim();
    }

    public void setShipToEmail(String shipToEmail) {
        if (shipToEmail != null)
            this.shipToEmail = shipToEmail.trim();
    }

    public void setOrderNumber(String orderNumber) {
        if (orderNumber != null)
            this.orderNumber = orderNumber.trim();
    }

    public void setAwbBl(String awbBl) {
        if (awbBl != null)
            this.awbBl = awbBl.trim();
    }

    public void setPackingList(String packingList) {
        if (packingList != null)
            this.packingList = packingList.trim();
    }

    public void setRemarks(String remarks) {
        if (remarks != null)
            this.remarks = remarks.trim();
    }

    public void setInternalRemarks(String internalRemarks) {
        if (internalRemarks != null)
            this.internalRemarks = internalRemarks.trim();
    }

    public void setClientId(UUID clientId) {
        if (clientId != null) {
            this.client = new ClientDTO();
            this.client.setId(clientId);
        }
    }

    public void setClientContactId(UUID clientContactId) {
        if (clientContactId != null) {
            this.clientContact = new ClientContactDTO();
            this.clientContact.setId(clientContactId);
        }
    }

    public void setShipToCityId(UUID shipToCityId) {
        if (shipToCityId != null) {
            this.shipToCity = new CityDTO();
            this.shipToCity.setId(shipToCityId);
        }
    }

    public void setSalesRepId(UUID salesRepId) {
        if (salesRepId != null) {
            this.salesRep = new UserDTO();
            this.salesRep.setId(salesRepId);
        }
    }

    public void setOpenById(UUID openById) {
        if (openById != null) {
            this.openBy = new UserDTO();
            this.openBy.setId(openById);
        }
    }
}
