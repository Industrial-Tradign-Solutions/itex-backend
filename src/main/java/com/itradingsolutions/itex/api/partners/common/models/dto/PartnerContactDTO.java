package com.itradingsolutions.itex.api.partners.common.models.dto;

import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public abstract class PartnerContactDTO<P extends PartnerContactPhoneDTO> extends BaseDTO {

    private String name;
    private String title;
    private String email;
    private boolean validMain;
    private boolean active;
    private List<P> listPhones;


    public String getMainPhone() {
        return listPhones == null || listPhones.isEmpty()
                ? ""
                : listPhones.stream()
                .filter(PartnerContactPhoneDTO::isValidMain)
                .map(PartnerContactPhoneDTO::getFullPhone)
                .findFirst()
                .orElse("");
    }
}
