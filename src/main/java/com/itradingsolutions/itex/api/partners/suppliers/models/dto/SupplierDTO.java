package com.itradingsolutions.itex.api.partners.suppliers.models.dto;

import com.itradingsolutions.itex.api.partners.common.models.dto.PartnerDTO;
import com.itradingsolutions.itex.api.partners.suppliers.models.enums.SupplierStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupplierDTO extends  PartnerDTO<SupplierStatus, SupplierInfoDepDTO>{

    private String wireAchInstructions;
}
