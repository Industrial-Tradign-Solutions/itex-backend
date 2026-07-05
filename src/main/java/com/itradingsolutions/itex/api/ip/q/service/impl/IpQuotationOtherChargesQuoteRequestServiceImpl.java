package com.itradingsolutions.itex.api.ip.q.service.impl;

import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;
import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.ip.q.exceptions.NotExistIpQuotationException;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationOtherChargesQuoteRequestDTO;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationOtherChargesQuoteRequestEntity;
import com.itradingsolutions.itex.api.ip.q.models.mapper.IpQuotationOtherChargesQuoteRequestMapper;
import com.itradingsolutions.itex.api.ip.q.models.request.IpQuotationOtherChargesImportItem;
import com.itradingsolutions.itex.api.ip.q.repository.IIpQuotationOtherChargesQuoteRequestRepository;
import com.itradingsolutions.itex.api.ip.q.repository.IIpQuotationsQuoteRequestRepository;
import com.itradingsolutions.itex.api.ip.q.service.IIpQuotationOtherChargesQuoteRequestService;
import com.itradingsolutions.itex.api.ip.q.service.IpQuotationService;
import com.itradingsolutions.itex.api.ip.q.models.response.QuotationAvailableQrOtherChargeResponse;
import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestOtherChargesEntity;
import com.itradingsolutions.itex.api.ip.qr.repositories.IIpQuoteRequestOtherChargesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IpQuotationOtherChargesQuoteRequestServiceImpl extends UtilServiceAbs implements IIpQuotationOtherChargesQuoteRequestService {

    private final IIpQuotationOtherChargesQuoteRequestRepository repository;
    private final IIpQuotationsQuoteRequestRepository qqrRepository;
    private final IIpQuoteRequestOtherChargesRepository qrOtherChargesRepository;
    private final IpQuotationOtherChargesQuoteRequestMapper mapper;
    private final IpQuotationService quotationService;
    private final IUserService userService;

    @Override
    @Transactional
    public List<IpQuotationOtherChargesQuoteRequestDTO> bulkImport(List<IpQuotationOtherChargesImportItem> items, UUID quotationId) {
        if (items == null || items.isEmpty()) return List.of();

        var quotation = quotationService.getEntityById(quotationId);
        quotationService.validateQuotationInCreatedStatus(quotation, userService.getUserAuthenticated());

        // 1. Validate no duplicate qrOtherChargeId within request
        var qrOtherChargeIds = items.stream()
                .map(IpQuotationOtherChargesImportItem::qrOtherChargeId)
                .toList();
        var uniqueIds = Set.copyOf(qrOtherChargeIds);
        if (uniqueIds.size() != qrOtherChargeIds.size()) {
            throw new BadRequestException(simpleMessage("ip.q.other-charges.imported-from-qr.duplicate-in-request"));
        }

        // 2. Validate all qrOtherChargeIds exist
        var qrOtherChargesMap = qrOtherChargesRepository.findAllById(uniqueIds).stream()
                .collect(Collectors.toMap(IpQuoteRequestOtherChargesEntity::getId, e -> e));
        for (var id : uniqueIds) {
            if (!qrOtherChargesMap.containsKey(id)) {
                throw new NotExistIpQuotationException(simpleMessage("ip.q.other-charges.imported-from-qr.qr-other-charge-not-found"));
            }
        }

        // 3. Load already imported QR other charge IDs
        var alreadyImported = repository.findImportedQrOtherChargeIdsByQuotationId(quotationId);

        // 4. Build entities for new imports only
        var entities = new ArrayList<IpQuotationOtherChargesQuoteRequestEntity>();
        for (var item : items) {
            if (alreadyImported.contains(item.qrOtherChargeId())) continue;

            var qqr = qqrRepository.findByIdAndQuotation_Id(item.quotationsQuoteRequestId(), quotationId)
                    .orElseThrow(() -> new NotExistIpQuotationException(simpleMessage("ip.q.not-exist")));

            var entity = new IpQuotationOtherChargesQuoteRequestEntity();
            entity.setQuotationsQuoteRequest(qqr);
            entity.setQrOtherCharge(qrOtherChargesMap.get(item.qrOtherChargeId()));
            entities.add(entity);
        }

        if (entities.isEmpty()) return List.of();

        var saved = repository.saveAll(entities);
        return saved.stream()
                .map(mapper::entityToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<IpQuotationOtherChargesQuoteRequestDTO> getImportedByQuotationId(UUID quotationId) {
        return repository.findAllByQuotationId(quotationId).stream()
                .map(mapper::entityToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public IpQuotationOtherChargesQuoteRequestDTO get(UUID id, UUID quotationId) {
        var entity = repository.findByIdAndQuotationsQuoteRequest_Quotation_Id(id, quotationId)
                .orElseThrow(() -> new NotFoundException(simpleMessage("ip.q.other-charges.imported-from-qr.not-found")));
        return mapper.entityToDto(entity);
    }

    @Override
    @Transactional
    public void remove(UUID id, UUID quotationId) {
        var entity = repository.findByIdAndQuotationsQuoteRequest_Quotation_Id(id, quotationId)
                .orElseThrow(() -> new NotFoundException(simpleMessage("ip.q.other-charges.imported-from-qr.not-found")));
        repository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuotationAvailableQrOtherChargeResponse> getAvailableQrOtherCharges(UUID quotationId) {
        var qqrs = qqrRepository.findByQuotation_Id(quotationId);
        if (qqrs.isEmpty()) return List.of();

        var qrIds = qqrs.stream()
                .map(qqr -> qqr.getQuoteRequest().getId())
                .collect(Collectors.toSet());

        var allQrOtherCharges = qrOtherChargesRepository.findByIpQuoteRequestIds(qrIds);
        if (allQrOtherCharges.isEmpty()) return List.of();

        var alreadyImportedIds = repository.findImportedQrOtherChargeIdsByQuotationId(quotationId);

        return allQrOtherCharges.stream()
                .filter(oc -> !alreadyImportedIds.contains(oc.getId()))
                .map(oc -> {
                    var qr = oc.getIpQuoteRequest();
                    return new QuotationAvailableQrOtherChargeResponse(
                            oc.getId(),
                            oc.getValue(),
                            oc.getDescription(),
                            qr != null ? qr.getNumber() : null
                    );
                })
                .toList();
    }
}
