package com.itradingsolutions.itex.api.ip.products.models.responses;

import com.itradingsolutions.itex.api.admin.user.models.responses.BasicUserResponse;
import com.itradingsolutions.itex.api.common.models.responses.BaseResponse;
import com.itradingsolutions.itex.api.common.util.models.enums.FreightClass;
import com.itradingsolutions.itex.api.ip.products.models.enums.IpProductStatus;
import com.itradingsolutions.itex.api.masters.brand.models.responses.BasicBrandResponse;
import com.itradingsolutions.itex.api.masters.location.models.responses.BasicCountryResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@SuperBuilder
public class IpProductResponse extends BaseResponse {

    private BasicBrandResponse brand;
    private String description;
    private String clientDescription;
    private String mfrReference;
    private String clientReference;
    private BigDecimal netWeightLbs;
    private Integer nmfc;
    private FreightClass freightClass;
    private IpProductStatus status;
    private String notes;
    private String keywords;
    private Integer htsScheduleBNumber;
    private String eccn;
    private BasicCountryResponse coo;
    private boolean battery;
    private boolean hazmat;
    private boolean dualUse;
    private IpProductResponse substituteProduct;
    private List<IpProductSurplusResponse> surplus;
    private ZonedDateTime openAt;
    private BasicUserResponse openBy;
    private BigDecimal totalSurplus;
    private IpProductSurplusResponse surplusLocation;
    private String name;
}
