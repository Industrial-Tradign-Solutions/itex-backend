package com.itradingsolutions.itex.api.ip.products.models.dto;

import com.itradingsolutions.itex.api.admin.user.models.dto.UserDTO;
import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;
import com.itradingsolutions.itex.api.common.util.models.enums.FreightClass;
import com.itradingsolutions.itex.api.ip.products.models.enums.IpProductStatus;
import com.itradingsolutions.itex.api.ip.products.models.enums.IpProductSurplusType;
import com.itradingsolutions.itex.api.masters.brand.models.dto.BrandDTO;
import com.itradingsolutions.itex.api.masters.location.models.dto.CountryDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@ToString
public class IpProductDTO extends BaseDTO {

    private BrandDTO brand;
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
    private CountryDTO coo;
    private boolean battery;
    private boolean hazmat;
    private boolean dualUse;
    private IpProductDTO substituteProduct;
    private List<IpProductSurplusDTO> surplus;
    private ZonedDateTime openAt;
    private UserDTO openBy;

    public String getName() {
        return this.description;
    }

    public void setClientReference(String clientReference) {
        if (clientReference != null)
            this.clientReference = normalizeText(clientReference).toUpperCase().trim();
    }

    public void setMfrReference(String mfrReference) {
        if (mfrReference != null)
            this.mfrReference = normalizeText(mfrReference).toUpperCase().trim();
    }

    public void setBrandId(UUID brandId) {
        if (brandId != null) {
            this.brand = new BrandDTO();
            this.brand.setId(brandId);
        }
    }

    public void setDescription(String description) {
        if (description != null)
            this.description = description.trim().toUpperCase();
    }

    public void setClientDescription(String clientDescription) {
        if (clientDescription != null)
            this.clientDescription = clientDescription.trim().toUpperCase();
    }

    public void setKeywords(String keywords) {
        this.keywords = normalizeText(keywords).trim();
    }

    public void setNotes(String notes) {
        this.notes = normalizeText(notes).trim();
    }

    public void setEccn(String eccn) {
        if (eccn != null)
            this.eccn = normalizeText(eccn).trim().toUpperCase();
    }

    public void setNetWeightLbs(String netWeightLbs) {
        if (netWeightLbs != null && !netWeightLbs.isEmpty())
            this.netWeightLbs = new BigDecimal(netWeightLbs);
    }

    public void setNetWeightLbs(BigDecimal netWeightLbs) {
        this.netWeightLbs = netWeightLbs;
    }

    public void setCooId(UUID cooId) {
        if (cooId != null) {
            this.coo = new CountryDTO();
            this.coo.setId(cooId);
        }
    }

    public void setCooStr(String coo) {
        if (coo != null && !coo.isEmpty()) {
            this.coo = new CountryDTO();
            this.coo.setNameShort(coo);
        }
    }

    public void setBrandStr(String brand) {
        if (brand != null && !brand.isEmpty()) {
            this.brand = new BrandDTO();
            this.brand.setName(brand);
        }
    }

    public void setOpenById(UUID openById) {
        this.openBy = new UserDTO();
        this.openBy.setId(openById);
    }

    public void setSurplus(List<IpProductSurplusDTO> surplus) {
        if (surplus != null) {
            surplus.sort(Comparator.comparing(IpProductSurplusDTO::getCreatedAt).reversed());
            this.surplus = surplus;
        }
    }

    public IpProductSurplusDTO getSurplusLocation() {
        if (getTotalSurplus().compareTo(BigDecimal.ZERO) == 0)
            return null;
        if (surplus == null || surplus.isEmpty())
            return null;
        return surplus.stream()
                .filter(s -> IpProductSurplusType.IN.equals(s.getType()))
                .findFirst()
                .orElse(null);
    }

    public BigDecimal getTotalSurplus() {
        return Optional.ofNullable(surplus)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(IpProductSurplusDTO::getQuantity)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
