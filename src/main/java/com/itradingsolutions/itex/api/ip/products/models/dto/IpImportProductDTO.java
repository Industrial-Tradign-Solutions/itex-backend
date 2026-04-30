package com.itradingsolutions.itex.api.ip.products.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class IpImportProductDTO extends IpProductDTO {

    private List<String> importErrors;
    private boolean validImport;
    private boolean saveBrand;
    private boolean saveCoo;
}
