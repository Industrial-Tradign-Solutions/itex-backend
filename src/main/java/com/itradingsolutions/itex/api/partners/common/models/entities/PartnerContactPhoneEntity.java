package com.itradingsolutions.itex.api.partners.common.models.entities;

import com.itradingsolutions.itex.api.common.models.entities.BaseEntity;
import com.itradingsolutions.itex.api.common.util.models.enums.PhoneType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

@Getter
@Setter
@ToString
@MappedSuperclass
public abstract class PartnerContactPhoneEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 6185849871271862221L;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private PhoneType type;

    @Column(name = "country_code", length = 3)
    private String countryCode;

    @Column(name = "city_code", length = 3)
    private String cityCode;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @Column(name = "ext", length = 5)
    private String ext;

    @Column(name = "is_main", nullable = false)
    private boolean validMain;

    public String getFullPhone() {
        if (phoneNumber == null || phoneNumber.isEmpty())
            return "";
        
        StringBuilder resp = new StringBuilder();
        if (this.countryCode != null) {
            resp.append("+").append(this.countryCode).append(" ");
        }

        if (this.cityCode != null) {
            resp.append("(").append(this.cityCode).append(") ");
        }

        resp.append(formatPhoneNumber(this.phoneNumber));
        if (this.ext != null) {
            resp.append(" ext. ").append(this.ext);
        }
        return resp.toString();
    }

    private String formatPhoneNumber(String number) {
        return number.substring(0, 3) + "-" + number.substring(3);
    }

}
