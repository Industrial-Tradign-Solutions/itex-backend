package com.itradingsolutions.itex.api.ip.qr.models.responses;

import com.itradingsolutions.itex.api.admin.user.models.responses.BasicUserResponse;
import com.itradingsolutions.itex.api.common.models.responses.BaseResponse;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.common.util.models.enums.FreightClass;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;
import com.itradingsolutions.itex.api.ip.qr.models.enums.IpQuoteRequestStatus;
import com.itradingsolutions.itex.api.partners.clients.models.responses.ClientContactResponse;
import com.itradingsolutions.itex.api.partners.clients.models.responses.ClientResponse;
import com.itradingsolutions.itex.api.partners.suppliers.models.responses.SupplierContactResponse;
import com.itradingsolutions.itex.api.partners.suppliers.models.responses.SupplierResponse;
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
public class IpQuoteRequestResponse extends BaseResponse {

    private String number;
    private IpQuoteRequestStatus status;
    private Currency currency;
    private ClientResponse client;
    private ClientContactResponse clientContact;
    private String clientQrNumber;
    private BasicUserResponse salesRep;
    private SupplierResponse supplier;
    private SupplierContactResponse supplierContact;
    private String supplierQrNumber;
    private String internalRemarks;
    private String remarks;
    private BigDecimal grossWeightLbs;
    private String shippingPointZipCode;
    private FreightClass freightClass;
    private String fobShippingPoint;
    private PaymentTerms paymentTerms;
    private BigDecimal freightCharges;
    private ZonedDateTime openAt;
    private BasicUserResponse openBy;
    private List<IpQuoteRequestProductResponse> products;
    private List<IpQuoteRequestOtherChargeResponse> otherCharges;
    private BigDecimal subTotal;
    private BigDecimal total;
    private IpQuoteRequestResponse clonedByQr;

    private List<ListIpQuoteRequestResponse> clonedQrs;
    private String name;
    private BigDecimal totalOtherCharges;

    private ZonedDateTime createdAt;
    private ZonedDateTime sentAt;
    private ZonedDateTime answeredAt;
    private ZonedDateTime completeAt;
    private ZonedDateTime rejectAt;
}
