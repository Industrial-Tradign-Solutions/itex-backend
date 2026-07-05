package com.itradingsolutions.itex.api.ip.qr.models.dto;

import com.itradingsolutions.itex.api.admin.user.models.dto.UserDTO;
import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.common.util.models.enums.FreightClass;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationDTO;
import com.itradingsolutions.itex.api.ip.qr.models.enums.IpQuoteRequestStatus;
import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientContactDTO;
import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientDTO;
import com.itradingsolutions.itex.api.partners.suppliers.models.dto.SupplierContactDTO;
import com.itradingsolutions.itex.api.partners.suppliers.models.dto.SupplierDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class IpQuoteRequestDTO extends BaseDTO {

    private String number ;
    private IpQuoteRequestStatus status ;
    private Currency currency;
    private ClientDTO client;
    private ClientContactDTO clientContact;
    private String clientQrNumber;

    private UserDTO salesRep;
    private SupplierDTO supplier;
    private SupplierContactDTO supplierContact ;
    private String supplierQrNumber ;

    private String internalRemarks;
    private String remarks;
    private String shippingPointZipCode ;
    private FreightClass freightClass ;
    private String fobShippingPoint ;
    private PaymentTerms paymentTerms ;
    private BigDecimal freightCharges ;
    private ZonedDateTime openAt ;
    private UserDTO openBy ;
    private List<IpQuoteRequestProductDTO> products;
    private List<IpQuoteRequestOtherChargesDTO> otherCharges ;
    private List<IpQuoteRequestDTO> clonedQrs;
    private IpQuoteRequestDTO clonedByQr;

    private ZonedDateTime sentAt;
    private ZonedDateTime answeredAt;
    private ZonedDateTime completeAt;
    private ZonedDateTime rejectAt;

    private List<IpQuotationDTO> listQuotations;

    public String getName() {
        return this.number;
    }

    public void setNumber(String number) {
        if (number != null)
            this.number = number.trim().toUpperCase();
    }

    public void setClientQrNumber(String clientQrNumber) {
        if (clientQrNumber != null)
            this.clientQrNumber = clientQrNumber.trim().toUpperCase();
    }

    public void setSupplierQrNumber(String supplierQrNumber) {
        if (supplierQrNumber != null)
            this.supplierQrNumber = supplierQrNumber.trim().toUpperCase();
    }

    public void setFobShippingPoint(String fobShippingPoint) {
        if (fobShippingPoint != null)
            this.fobShippingPoint = fobShippingPoint.trim();
    }

    public void setShippingPointZipCode(String shippingPointZipCode) {
        if (shippingPointZipCode != null)
            this.shippingPointZipCode = shippingPointZipCode.trim();
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
            this.client =  new ClientDTO();
            this.client.setId(clientId);
        }
    }

    public void setSupplierId(UUID supplierId) {
        if (supplierId != null) {
            this.supplier =  new SupplierDTO();
            this.supplier.setId(supplierId);
        }
    }

    public void setClientContactId(UUID clientContactId) {
        if (clientContactId != null) {
            this.clientContact =  new ClientContactDTO();
            this.clientContact.setId(clientContactId);
        }
    }


    public void setSupplierContactId(UUID supplierContactId) {
        if (supplierContactId != null) {
            this.supplierContact =  new SupplierContactDTO();
            this.supplierContact.setId(supplierContactId);
        }
    }

    public void setSalesRepId(UUID salesRepId) {
        if (salesRepId != null) {
            this.salesRep = new UserDTO();
            this.salesRep.setId(salesRepId);
        }
    }

    public void setFreightCharges(BigDecimal freightCharges) {
        this.freightCharges = Objects.requireNonNullElse(freightCharges, BigDecimal.ZERO).setScale(5, RoundingMode.HALF_UP);
    }

    public BigDecimal getSubTotal() {
        return Optional.ofNullable(this.products)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(IpQuoteRequestProductDTO::getExtendedPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotal() {
        return Stream.of(getSubTotal(), this.freightCharges, this.getTotalOtherCharges())
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getGrossWeightLbs() {
        return Optional.ofNullable(this.products)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(IpQuoteRequestProductDTO::getGrossWeightLbs)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalOtherCharges() {
        return Optional.ofNullable(this.otherCharges)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(IpQuoteRequestOtherChargesDTO::getValue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

    }

    /* Variable para validar si se puede poner en este status*/
    private boolean validAnswered;
}
