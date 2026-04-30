package com.itradingsolutions.itex.api.partners.suppliers.models.requests;

import com.itradingsolutions.itex.api.partners.common.models.requests.PartnerRequest;
import com.itradingsolutions.itex.api.partners.suppliers.models.enums.SupplierStatus;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SupplierRequest extends PartnerRequest<SupplierStatus, SupplierInfoDepRequest> {

    private String wireAchInstructions;
}
