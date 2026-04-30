package com.itradingsolutions.itex.api.ip.products.models.responses;

import com.itradingsolutions.itex.api.common.models.responses.BaseResponse;
import com.itradingsolutions.itex.api.common.util.models.enums.FreightClass;
import com.itradingsolutions.itex.api.ip.products.models.enums.IpProductStatus;
import com.itradingsolutions.itex.api.masters.brand.models.responses.BasicBrandResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class ListIpProductResponse extends BaseResponse {
    private String description;
    private BasicBrandResponse brand;
    private FreightClass freightClass;
    private Integer nmfc;
    private String mfrReference;
    private String clientReference;
    private IpProductStatus status;
    private String name;
}
