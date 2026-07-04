package com.itradingsolutions.itex.api.ip.q.models.response;

import com.itradingsolutions.itex.api.admin.user.models.responses.BasicUserResponse;
import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.common.models.responses.BaseResponse;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.common.util.models.enums.Incoterms;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;
import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationStatus;
import com.itradingsolutions.itex.api.ip.qr.models.responses.BasicIpQuoteRequestResponse;
import com.itradingsolutions.itex.api.partners.clients.models.responses.ClientContactResponse;
import com.itradingsolutions.itex.api.partners.clients.models.responses.ClientResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class IpQuotationResponse extends BaseResponse {

    private String number;
    private String name;
    private IpQuotationStatus status;
    private Currency currency;
    private ClientResponse client;
    private ClientContactResponse clientContact;
    private String clientQNumber;
    private BasicUserResponse salesRep;
    private String remarks;
    private String internalRemarks;
    private Integer leadTime;
    private LeadTime leadTimeType;
    private Integer validity;
    private LeadTime validityType;
    private Incoterms incoterms;
    private PaymentTerms paymentTerms;
    private ZonedDateTime applicationAt;
    private BasicUserResponse openBy;
    private ZonedDateTime openAt;
    private ZonedDateTime sentAt;
    private ZonedDateTime answeredAt;
    private ZonedDateTime completeAt;
    private ZonedDateTime rejectAt;
    private List<BasicIpQuoteRequestResponse> listQuoteRequests;
    private List<IpQuotationProductResponse> products;
    private List<IpQuotationOtherChargeResponse> otherCharges;
    private List<IpQuotationOtherChargesQuoteRequestResponse> qrOtherCharges;
    private IpQuotationResponse clonedByQuotation;

    private List<ListIpQuotationResponse> clonedQuotations;


    private BigDecimal grossWeightLbs;

    private BigDecimal totalOtherCharges;
    private BigDecimal subTotal;
    private BigDecimal total;
    private BigDecimal freightCharges;
}
