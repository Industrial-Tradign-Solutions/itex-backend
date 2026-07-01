package com.itradingsolutions.itex.api.ip.q.models.dto;

import com.itradingsolutions.itex.api.admin.user.models.dto.UserDTO;
import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;
import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.common.util.models.enums.Incoterms;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;
import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationStatus;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationsQuoteRequestSummaryDTO;
import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestOtherChargesDTO;
import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestProductDTO;
import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientContactDTO;
import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientDTO;
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
import java.util.stream.Stream;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class IpQuotationDTO extends BaseDTO {

    private String number;
    private IpQuotationStatus status;
    private Currency currency;
    private ClientDTO client;
    private ClientContactDTO clientContact;
    private String clientQNumber;
    private UserDTO salesRep;
    private String remarks;
    private String internalRemarks;
    private Integer leadTime;
    private LeadTime leadTimeType;
    private Integer validity;
    private LeadTime validityType;
    private Incoterms incoterms;
    private PaymentTerms paymentTerms;
    private ZonedDateTime applicationAt;
    private String pdfUrl;
    private UserDTO openBy;
    private ZonedDateTime openAt;
    private ZonedDateTime sentAt;
    private ZonedDateTime answeredAt;
    private ZonedDateTime completeAt;
    private ZonedDateTime rejectAt;
    private List<IpQuotationsQuoteRequestSummaryDTO> listQuoteRequests;
    private List<IpQuotationProductDTO> products;
    private List<IpQuotationOtherChargeDTO> otherCharges;
    private IpQuotationDTO clonedByQuotation;
    private List<IpQuotationDTO> clonedQuotations;


    public String getName() {
        return this.number;
    }



    public BigDecimal getTotalOtherCharges() {
        if (otherCharges == null || otherCharges.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return otherCharges.stream()
                .map(IpQuotationOtherChargeDTO::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getSubTotal() {
        return Optional.ofNullable(this.products)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(IpQuotationProductDTO::getSellingExtendedPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getFreightCharges() {
        return BigDecimal.ZERO;
    }

    public BigDecimal getTotal() {
        return Stream.of(getSubTotal(), getFreightCharges(), getTotalOtherCharges())
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getGrossWeightLbs() {
        return BigDecimal.TWO;
    }
}
