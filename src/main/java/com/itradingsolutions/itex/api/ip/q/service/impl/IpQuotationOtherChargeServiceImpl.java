package com.itradingsolutions.itex.api.ip.q.service.impl;

import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.ip.q.exceptions.IpQuotationOtherChargeExistException;
import com.itradingsolutions.itex.api.ip.q.exceptions.NotExistIpQuotationException;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationOtherChargeDTO;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationOtherChargeEntity;
import com.itradingsolutions.itex.api.ip.q.models.mapper.IpQuotationOtherChargeMapper;
import com.itradingsolutions.itex.api.ip.q.repository.IIpQuotationOtherChargeRepository;
import com.itradingsolutions.itex.api.ip.q.service.IIpQuotationOtherChargeService;
import com.itradingsolutions.itex.api.ip.q.service.IpQuotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementation of {@link IIpQuotationOtherChargeService}.
 * <p>
 * Handles business logic for creating, updating, retrieving, and removing
 * other charges from quotations. Validates that quotations are in CREATED status
 * before allowing modifications.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class IpQuotationOtherChargeServiceImpl extends UtilServiceAbs implements IIpQuotationOtherChargeService {

    private final IpQuotationOtherChargeMapper otherChargeMapper;
    private final IIpQuotationOtherChargeRepository otherChargeRepository;
    private final IpQuotationService quotationService;
    private final IUserService userService;

    @Override
    @Transactional
    public IpQuotationOtherChargeDTO create(IpQuotationOtherChargeDTO request, UUID quotationId) {
        if (otherChargeRepository.existsDescription(request.getDescription(), quotationId))
            throw new IpQuotationOtherChargeExistException(simpleMessage("ip.q.other-charges.exist"));

        var entity = new IpQuotationOtherChargeEntity();
        entity.setIpQuotation(quotationService.getEntityById(quotationId));
        return saveOtherCharge(request, entity);
    }

    @Override
    @Transactional
    public IpQuotationOtherChargeDTO update(IpQuotationOtherChargeDTO request, UUID otherChargeId, UUID quotationId) {
        var entity = findById(otherChargeId, quotationId);
        if (otherChargeRepository.existsDescription(request.getDescription(), quotationId, otherChargeId))
            throw new IpQuotationOtherChargeExistException(simpleMessage("ip.q.other-charges.exist"));
        return saveOtherCharge(request, entity);
    }

    @Override
    @Transactional(readOnly = true)
    public IpQuotationOtherChargeDTO get(UUID otherChargeId, UUID quotationId) {
        return otherChargeMapper.entityToDto(findById(otherChargeId, quotationId));
    }

    @Override
    @Transactional
    public void remove(UUID otherChargeId, UUID quotationId) {
        otherChargeRepository.deleteById(quotationId, otherChargeId);
    }

    /**
     * Saves an other charge entity after validating that the quotation is in CREATED status.
     *
     * @param request the other charge data
     * @param entity the entity to save
     * @return the saved other charge DTO
     */
    private IpQuotationOtherChargeDTO saveOtherCharge(IpQuotationOtherChargeDTO request, IpQuotationOtherChargeEntity entity) {
        quotationService.validateQuotationInCreatedStatus(entity.getIpQuotation(), userService.getUserAuthenticated());
        entity.setDescription(request.getDescription());
        entity.setValue(request.getValue());
        return otherChargeMapper.entityToDto(otherChargeRepository.save(entity));
    }

    /**
     * Finds an other charge by ID and quotation ID.
     *
     * @param otherChargeId the other charge ID
     * @param quotationId the quotation ID
     * @return the entity
     * @throws NotExistIpQuotationException if not found
     */
    private IpQuotationOtherChargeEntity findById(UUID otherChargeId, UUID quotationId) {
        return otherChargeRepository.fetchOneById(otherChargeId, quotationId)
                .orElseThrow(() -> new NotExistIpQuotationException(simpleMessage("ip.q.not-exist")));
    }
}
