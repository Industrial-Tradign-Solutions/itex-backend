package com.itradingsolutions.itex.api.ip.po.models.response;

import com.itradingsolutions.itex.api.admin.user.models.responses.BasicUserResponse;
import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.common.models.responses.BaseResponse;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;
import com.itradingsolutions.itex.api.ip.po.models.enums.PurchaseOrderStatus;
import com.itradingsolutions.itex.api.masters.location.models.responses.CityResponse;
import com.itradingsolutions.itex.api.partners.clients.models.responses.ClientResponse;
import com.itradingsolutions.itex.api.partners.suppliers.models.responses.SupplierResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderResponse extends BaseResponse {

    private String number;
    private String name;
    private PurchaseOrderStatus status;
    private Currency currency;
    private ClientResponse client;
    private SupplierResponse supplier;
    private BasicUserResponse salesRep;
    private BasicUserResponse openBy;
    private PaymentTerms paymentTerms;
    private Integer leadTime;
    private LeadTime leadTimeType;
    private String shipToName;
    private String shipToAddress;
    private CityResponse shipToCity;
    private String shipToPhone;
    private String shipToContactName;
    private String shipToEmail;
    private BigDecimal salesTax;
    private ZonedDateTime openAt;
}
