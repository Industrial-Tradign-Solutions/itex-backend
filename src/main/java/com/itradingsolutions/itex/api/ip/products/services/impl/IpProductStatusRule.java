package com.itradingsolutions.itex.api.ip.products.services.impl;

import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.ip.products.exceptions.ProductStatusChangeException;
import com.itradingsolutions.itex.api.ip.products.models.dto.IpProductDTO;
import com.itradingsolutions.itex.api.ip.products.models.enums.IpProductStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class IpProductStatusRule extends UtilServiceAbs {

    public boolean isCompleteForActivation(IpProductDTO dto) {
        return dto.getBrand() != null
                && hasText(dto.getDescription())
                && hasText(dto.getClientDescription())
                && hasText(dto.getMfrReference())
                && hasText(dto.getClientReference())
                && dto.getNetWeightLbs() != null;
    }

    public void validateTransition(IpProductStatus currentStatus, IpProductStatus newStatus, IpProductDTO dto) {
        if (currentStatus == newStatus)
            throw new ProductStatusChangeException(simpleMessage("ip.product.equal-status"));

        switch (newStatus) {
            case ACTIVE -> {
                if (currentStatus != IpProductStatus.DRAFT && currentStatus != IpProductStatus.INACTIVE)
                    throw invalidTransition(currentStatus, newStatus);
                if (!isCompleteForActivation(dto))
                    throw new ProductStatusChangeException(simpleMessage("ip.product.not-complete-active"));
            }
            case DRAFT -> {
                if (currentStatus != IpProductStatus.INACTIVE)
                    throw invalidTransition(currentStatus, newStatus);
            }
            case INACTIVE -> {
                if (currentStatus != IpProductStatus.ACTIVE && currentStatus != IpProductStatus.DRAFT)
                    throw invalidTransition(currentStatus, newStatus);
            }
        }
    }

    private ProductStatusChangeException invalidTransition(IpProductStatus currentStatus, IpProductStatus newStatus) {
        return new ProductStatusChangeException(compositeMessage(
                "ip.product.invalid-transition",
                new String[]{currentStatus.name(), newStatus.name()}
        ));
    }

    private boolean hasText(String value) {
        return StringUtils.hasText(value);
    }
}