package com.itradingsolutions.itex.api.ip.qr.service.impl;

import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.ip.qr.exceptions.NotExistIpQuoteRequestException;
import com.itradingsolutions.itex.api.ip.qr.exceptions.QrOtherChargeExistException;
import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestOtherChargesDTO;
import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestOtherChargesEntity;
import com.itradingsolutions.itex.api.ip.qr.models.mappers.IpQuoteRequestOtherChargeMapper;
import com.itradingsolutions.itex.api.ip.qr.repositories.IIpQuoteRequestOtherChargesRepository;
import com.itradingsolutions.itex.api.ip.qr.service.IIpQuoteRequestOtherChargeService;
import com.itradingsolutions.itex.api.ip.qr.service.IIpQuoteRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IpQuoteRequestOtherChargeServiceImpl extends UtilServiceAbs implements IIpQuoteRequestOtherChargeService {

    private final IpQuoteRequestOtherChargeMapper qrOtherChargesMapper;
    private final IIpQuoteRequestOtherChargesRepository qrOtherChargesRepository;
    private final IIpQuoteRequestService qrService;
    private final IUserService userService;

    @Override
    @Transactional
    public IpQuoteRequestOtherChargesDTO create(IpQuoteRequestOtherChargesDTO request, UUID qrId) {
        if (qrOtherChargesRepository.existsDescription(request.getDescription(), qrId))
            throw new QrOtherChargeExistException(simpleMessage("ip.qr.other-charges.exist"));
        var entity = new IpQuoteRequestOtherChargesEntity();
        entity.setIpQuoteRequest(qrService.getEntityById(qrId));
        return saveQrOtherCharge(request, entity);
    }

    @Override
    @Transactional
    public IpQuoteRequestOtherChargesDTO update(IpQuoteRequestOtherChargesDTO request, UUID qrOtherChargeId, UUID qrId) {
        var entity = findById(qrOtherChargeId, qrId);
        if (qrOtherChargesRepository.existsDescription(request.getDescription(), qrId, qrOtherChargeId))
            throw new QrOtherChargeExistException(simpleMessage("ip.qr.other-charges.exist"));
        return saveQrOtherCharge(request, entity);
    }

    @Override
    @Transactional(readOnly = true)
    public IpQuoteRequestOtherChargesDTO get(UUID qrOtherChargeId, UUID qrId) {
        return qrOtherChargesMapper.entityToDto(findById(qrOtherChargeId, qrId));
    }

    @Override
    @Transactional
    public void remove(UUID qrOtherChargeId, UUID qrId) {
        qrOtherChargesRepository.deleteById(qrId, qrOtherChargeId);
    }

    private IpQuoteRequestOtherChargesDTO saveQrOtherCharge(IpQuoteRequestOtherChargesDTO request, IpQuoteRequestOtherChargesEntity entity) {
        qrService.validateOpenQR(entity.getIpQuoteRequest(), userService.getUserAuthenticated());
        entity.setDescription(request.getDescription());
        entity.setValue(request.getValue());
        return qrOtherChargesMapper.entityToDto(qrOtherChargesRepository.save(entity));
    }

    private IpQuoteRequestOtherChargesEntity findById(UUID idQrOtherCharge, UUID qrId) {
        return qrOtherChargesRepository.fetchOneById(idQrOtherCharge, qrId).orElseThrow(() -> new NotExistIpQuoteRequestException(simpleMessage("ip.qr.not-exist")));
    }
}
