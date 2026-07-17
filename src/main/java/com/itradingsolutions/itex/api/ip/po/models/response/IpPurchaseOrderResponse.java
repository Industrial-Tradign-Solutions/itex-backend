package com.itradingsolutions.itex.api.ip.po.models.response;

import com.itradingsolutions.itex.api.admin.user.models.responses.BasicUserResponse;
import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.common.models.responses.BaseResponse;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;
import com.itradingsolutions.itex.api.ip.po.models.enums.IpPurchaseOrderStatus;
import com.itradingsolutions.itex.api.ip.q.models.response.BasicQuotationResponse;
import com.itradingsolutions.itex.api.masters.location.models.responses.CityResponse;
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
public class IpPurchaseOrderResponse extends BaseResponse {

    private String number;
    private String name;
    private IpPurchaseOrderStatus status;
    private Currency currency;
    private BasicQuotationResponse quotation;
    private ClientResponse client;
    private ClientContactResponse clientContact;
    private String clientPoNumber;
    private BasicUserResponse salesRep;
    private SupplierResponse supplier;
    private SupplierContactResponse supplierContact;
    private String supplierPoNumber;
    private BasicUserResponse openBy;
    private PaymentTerms paymentTerms;
    private Integer leadTime;
    private LeadTime leadTimeType;
    private String shippingMethod;
    private String remarks;
    private String internalRemarks;
    private String shipToName;
    private String shipToAddress;
    private CityResponse shipToCity;
    private String shipToPhone;
    private String shipToContactName;
    private String shipToEmail;
    private BigDecimal salesTax;
    private BigDecimal subTotal;
    private BigDecimal totalOtherCharges;
    private BigDecimal total;
    private String pdfUrl;
    private ZonedDateTime openAt;
    private ZonedDateTime sentAt;
    private ZonedDateTime answeredAt;
    private ZonedDateTime completeAt;
    private ZonedDateTime rejectAt;

    private List<IpPurchaseOrderProductResponse> products;
    private List<IpPurchaseOrderOtherChargeResponse> otherCharges;
    private List<IpPurchaseOrderOtherChargesQuotationResponse> importedQuotationCharges;
    private List<IpPurchaseOrderOtherChargesQuotationQrResponse> importedQuoteRequestCharges;
}