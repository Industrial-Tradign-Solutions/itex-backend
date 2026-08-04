package com.itradingsolutions.itex.api.sales.invoices.models.response;

import com.itradingsolutions.itex.api.admin.user.models.responses.BasicUserResponse;
import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveDepartment;
import com.itradingsolutions.itex.api.common.models.responses.BaseResponse;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.common.util.models.enums.Incoterms;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;
import com.itradingsolutions.itex.api.ip.po.models.response.BasicIpPurchaseOrderResponse;
import com.itradingsolutions.itex.api.masters.location.models.responses.CityResponse;
import com.itradingsolutions.itex.api.partners.clients.models.responses.ClientContactResponse;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceStatus;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceVia;
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
public class InvoiceResponse extends BaseResponse {

    private String draftNumber;
    private String number;
    private String name;
    private ConsecutiveDepartment department;
    private InvoiceStatus status;
    private Currency currency;
    private InvoiceClientResponse client;
    private ClientContactResponse clientContact;
    private String shipToName;
    private String shipToAddress;
    private CityResponse shipToCity;
    private String shipToPhone;
    private String shipToContactName;
    private String shipToEmail;
    private String orderNumber;
    private InvoiceVia via;
    private Incoterms incoterms;
    private PaymentTerms paymentTerms;
    private String awbBl;
    private BasicUserResponse salesRep;
    private String remarks;
    private String internalRemarks;
    private String packingList;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal balanceDue;
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
    private BasicUserResponse openBy;

    private List<InvoiceProductResponse> products;
    private List<InvoiceChargeResponse> charges;
    private List<InvoiceTaxResponse> taxes;
    private List<BasicIpPurchaseOrderResponse> linkedPurchaseOrders;
    private BigDecimal productsTotal;
    private BigDecimal chargesTotal;
    private BigDecimal taxesTotal;
}
