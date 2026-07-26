package com.itradingsolutions.itex.api.ip.po.service.impl;

import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import com.itradingsolutions.itex.api.common.models.entities.BaseEntity;
import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.ip.po.exceptions.IpPurchaseOrderNotEditableException;
import com.itradingsolutions.itex.api.ip.po.exceptions.IpPurchaseOrderQuotationNotAssignedException;
import com.itradingsolutions.itex.api.ip.po.exceptions.NotExistIpPurchaseOrderException;
import com.itradingsolutions.itex.api.ip.po.exceptions.NotOpenIpPurchaseOrderException;
import com.itradingsolutions.itex.api.ip.po.models.dto.IpPurchaseOrderOtherChargeDTO;
import com.itradingsolutions.itex.api.ip.po.models.dto.IpPurchaseOrderOtherChargesQuotationDTO;
import com.itradingsolutions.itex.api.ip.po.models.dto.IpPurchaseOrderOtherChargesQuotationQrDTO;
import com.itradingsolutions.itex.api.ip.po.models.entities.IpPurchaseOrderEntity;
import com.itradingsolutions.itex.api.ip.po.models.entities.IpPurchaseOrderOtherChargeEntity;
import com.itradingsolutions.itex.api.ip.po.models.entities.IpPurchaseOrderOtherChargesQuotationEntity;
import com.itradingsolutions.itex.api.ip.po.models.entities.IpPurchaseOrderOtherChargesQuotationQrEntity;
import com.itradingsolutions.itex.api.ip.po.models.enums.IpPurchaseOrderStatus;
import com.itradingsolutions.itex.api.ip.po.models.mapper.IpPurchaseOrderOtherChargeMapper;
import com.itradingsolutions.itex.api.ip.po.models.mapper.IpPurchaseOrderOtherChargesQuotationMapper;
import com.itradingsolutions.itex.api.ip.po.models.mapper.IpPurchaseOrderOtherChargesQuotationQrMapper;
import com.itradingsolutions.itex.api.ip.po.models.response.AvailableIpPurchaseOrderOtherChargeResponse;
import com.itradingsolutions.itex.api.ip.po.repository.IIpPurchaseOrderOtherChargeRepository;
import com.itradingsolutions.itex.api.ip.po.repository.IIpPurchaseOrderOtherChargesQuotationQrRepository;
import com.itradingsolutions.itex.api.ip.po.repository.IIpPurchaseOrderOtherChargesQuotationRepository;
import com.itradingsolutions.itex.api.ip.po.repository.IIpPurchaseOrderRepository;
import com.itradingsolutions.itex.api.ip.po.service.IIpPurchaseOrderOtherChargeService;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationOtherChargesQuoteRequestEntity;
import com.itradingsolutions.itex.api.ip.q.repository.IIpQuotationOtherChargeRepository;
import com.itradingsolutions.itex.api.ip.q.repository.IIpQuotationOtherChargesQuoteRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IpPurchaseOrderOtherChargeServiceImpl extends UtilServiceAbs implements IIpPurchaseOrderOtherChargeService {

    private final IIpPurchaseOrderRepository poRepository;
    private final IIpPurchaseOrderOtherChargeRepository ownChargeRepository;
    private final IIpPurchaseOrderOtherChargesQuotationRepository importedQRepository;
    private final IIpPurchaseOrderOtherChargesQuotationQrRepository importedQrRepository;
    private final IIpQuotationOtherChargeRepository qOwnChargeRepository;
    private final IIpQuotationOtherChargesQuoteRequestRepository qQrChargeRepository;
    private final IpPurchaseOrderOtherChargeMapper ownMapper;
    private final IpPurchaseOrderOtherChargesQuotationMapper importedQMapper;
    private final IpPurchaseOrderOtherChargesQuotationQrMapper importedQrMapper;
    private final IUserService userService;

    @Override
    @Transactional
    public IpPurchaseOrderOtherChargeDTO create(IpPurchaseOrderOtherChargeDTO request, UUID poId) {
        var po = findEntityById(poId);
        validateOpenPO(po, userService.getUserAuthenticated());
        validateEditable(po);
        if (ownChargeRepository.existsDescription(request.getDescription(), poId))
            throw new BadRequestException(simpleMessage("ip.po.other-charges.exist"));

        var entity = new IpPurchaseOrderOtherChargeEntity();
        entity.setPurchaseOrder(po);
        entity.setDescription(request.getDescription());
        entity.setValue(request.getValue());
        return ownMapper.entityToDto(ownChargeRepository.save(entity));
    }

    @Override
    @Transactional
    public IpPurchaseOrderOtherChargeDTO update(IpPurchaseOrderOtherChargeDTO request, UUID otherChargeId, UUID poId) {
        var po = findEntityById(poId);
        validateOpenPO(po, userService.getUserAuthenticated());
        validateEditable(po);
        var entity = findOwnChargeById(otherChargeId, poId);
        if (ownChargeRepository.existsDescription(request.getDescription(), poId, otherChargeId))
            throw new BadRequestException(simpleMessage("ip.po.other-charges.exist"));

        entity.setDescription(request.getDescription());
        entity.setValue(request.getValue());
        return ownMapper.entityToDto(ownChargeRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public IpPurchaseOrderOtherChargeDTO get(UUID otherChargeId, UUID poId) {
        return ownMapper.entityToDto(findOwnChargeById(otherChargeId, poId));
    }

    @Override
    @Transactional
    public void remove(UUID otherChargeId, UUID poId) {
        var po = findEntityById(poId);
        validateOpenPO(po, userService.getUserAuthenticated());
        validateEditable(po);
        findOwnChargeById(otherChargeId, poId);
        ownChargeRepository.deleteById(poId, otherChargeId);
    }

    @Override
    @Transactional
    public List<IpPurchaseOrderOtherChargesQuotationDTO> importFromQuotation(List<UUID> quotationOtherChargeIds, UUID poId) {
        var po = findEntityById(poId);
        validateOpenPO(po, userService.getUserAuthenticated());
        validateEditable(po);
        validateQuotationAssigned(po);

        var alreadyImported = importedQRepository.findImportedChargeIdsByPurchaseOrderId(poId);
        var saved = bulkImportGeneric(quotationOtherChargeIds, qOwnChargeRepository, importedQRepository, alreadyImported,
                (purchaseOrder, source) -> {
                    var link = new IpPurchaseOrderOtherChargesQuotationEntity();
                    link.setPurchaseOrder(purchaseOrder);
                    link.setQuotationOtherCharge(source);
                    return link;
                }, po,
                "ip.po.other-charges.imported-from-quotation.duplicate-in-request",
                "ip.po.other-charges.imported-from-quotation.charge-not-found");

        return saved.stream().map(importedQMapper::entityToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailableIpPurchaseOrderOtherChargeResponse> getAvailableFromQuotation(UUID poId) {
        var po = findEntityById(poId);
        return Optional.ofNullable(po.getQuotation())
                .map(quotation -> {
                    var alreadyImported = importedQRepository.findImportedChargeIdsByPurchaseOrderId(poId);
                    return qOwnChargeRepository.findByIpQuotation_Id(quotation.getId()).stream()
                            .filter(charge -> !alreadyImported.contains(charge.getId()))
                            .map(charge -> new AvailableIpPurchaseOrderOtherChargeResponse(
                                    charge.getId(), charge.getValue(), charge.getDescription(),
                                    charge.getIpQuotation().getNumber()))
                            .toList();
                })
                .orElseGet(List::of);
    }

    @Override
    @Transactional(readOnly = true)
    public IpPurchaseOrderOtherChargesQuotationDTO getImportedFromQuotation(UUID id, UUID poId) {
        var entity = importedQRepository.findByIdAndPurchaseOrder_Id(id, poId)
                .orElseThrow(() -> new NotExistIpPurchaseOrderException(
                        simpleMessage("ip.po.other-charges.imported-from-quotation.not-found")));
        return importedQMapper.entityToDto(entity);
    }

    @Override
    @Transactional
    public void removeImportedFromQuotation(UUID id, UUID poId) {
        var entity = importedQRepository.findByIdAndPurchaseOrder_Id(id, poId)
                .orElseThrow(() -> new NotExistIpPurchaseOrderException(
                        simpleMessage("ip.po.other-charges.imported-from-quotation.not-found")));
        importedQRepository.delete(entity);
    }

    @Override
    @Transactional
    public List<IpPurchaseOrderOtherChargesQuotationQrDTO> importFromQuotationQr(List<UUID> quotationQrOtherChargeIds, UUID poId) {
        var po = findEntityById(poId);
        validateOpenPO(po, userService.getUserAuthenticated());
        validateEditable(po);
        validateQuotationAssigned(po);

        var alreadyImported = importedQrRepository.findImportedChargeIdsByPurchaseOrderId(poId);
        var saved = bulkImportGeneric(quotationQrOtherChargeIds, qQrChargeRepository, importedQrRepository, alreadyImported,
                (purchaseOrder, source) -> {
                    var link = new IpPurchaseOrderOtherChargesQuotationQrEntity();
                    link.setPurchaseOrder(purchaseOrder);
                    link.setQuotationQrOtherCharge(source);
                    return link;
                }, po,
                "ip.po.other-charges.imported-from-quotation-qr.duplicate-in-request",
                "ip.po.other-charges.imported-from-quotation-qr.charge-not-found");

        return saved.stream().map(importedQrMapper::entityToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailableIpPurchaseOrderOtherChargeResponse> getAvailableFromQuotationQr(UUID poId) {
        var po = findEntityById(poId);
        return Optional.ofNullable(po.getQuotation())
                .map(quotation -> {
                    var alreadyImported = importedQrRepository.findImportedChargeIdsByPurchaseOrderId(poId);
                    return qQrChargeRepository.findAllByQuotationId(quotation.getId()).stream()
                            .filter(charge -> !alreadyImported.contains(charge.getId()))
                            .map(this::toAvailableResponse)
                            .toList();
                })
                .orElseGet(List::of);
    }

    @Override
    @Transactional(readOnly = true)
    public IpPurchaseOrderOtherChargesQuotationQrDTO getImportedFromQuotationQr(UUID id, UUID poId) {
        var entity = importedQrRepository.findByIdAndPurchaseOrder_Id(id, poId)
                .orElseThrow(() -> new NotExistIpPurchaseOrderException(
                        simpleMessage("ip.po.other-charges.imported-from-quotation-qr.not-found")));
        return importedQrMapper.entityToDto(entity);
    }

    @Override
    @Transactional
    public void removeImportedFromQuotationQr(UUID id, UUID poId) {
        var entity = importedQrRepository.findByIdAndPurchaseOrder_Id(id, poId)
                .orElseThrow(() -> new NotExistIpPurchaseOrderException(
                        simpleMessage("ip.po.other-charges.imported-from-quotation-qr.not-found")));
        importedQrRepository.delete(entity);
    }

    private AvailableIpPurchaseOrderOtherChargeResponse toAvailableResponse(IpQuotationOtherChargesQuoteRequestEntity charge) {
        var qrOtherCharge = charge.getQrOtherCharge();
        var qqr = charge.getQuotationsQuoteRequest();
        var qrNumber = qqr != null && qqr.getQuoteRequest() != null ? qqr.getQuoteRequest().getNumber() : null;
        return new AvailableIpPurchaseOrderOtherChargeResponse(
                charge.getId(),
                qrOtherCharge != null ? qrOtherCharge.getValue() : null,
                qrOtherCharge != null ? qrOtherCharge.getDescription() : null,
                qrNumber
        );
    }

    /**
     * Generic bulk-import pipeline shared by both charge-import flavors (from a Quotation's own
     * charges, or from a Quotation's QR-imported charges): validates no duplicate ids in the
     * request, validates every id resolves to a real source entity, then builds and persists one
     * link entity per requested id that is not already imported (silently skipped otherwise).
     */
    private <S extends BaseEntity, L extends BaseEntity> List<L> bulkImportGeneric(
            List<UUID> requestedIds,
            JpaRepository<S, UUID> sourceRepository,
            JpaRepository<L, UUID> linkRepository,
            Set<UUID> alreadyImportedIds,
            BiFunction<IpPurchaseOrderEntity, S, L> linkFactory,
            IpPurchaseOrderEntity po,
            String duplicateKey,
            String notFoundKey) {
        if (requestedIds == null || requestedIds.isEmpty()) return List.of();

        var uniqueIds = Set.copyOf(requestedIds);
        if (uniqueIds.size() != requestedIds.size())
            throw new BadRequestException(simpleMessage(duplicateKey));

        var sourceMap = sourceRepository.findAllById(uniqueIds).stream()
                .collect(Collectors.toMap(BaseEntity::getId, source -> source));
        if (sourceMap.size() != uniqueIds.size())
            throw new NotExistIpPurchaseOrderException(simpleMessage(notFoundKey));

        var entities = requestedIds.stream()
                .filter(id -> !alreadyImportedIds.contains(id))
                .map(id -> linkFactory.apply(po, sourceMap.get(id)))
                .toList();

        if (entities.isEmpty()) return List.of();
        return linkRepository.saveAll(entities);
    }

    private void validateQuotationAssigned(IpPurchaseOrderEntity po) {
        Optional.ofNullable(po.getQuotation())
                .orElseThrow(() -> new IpPurchaseOrderQuotationNotAssignedException(
                        simpleMessage("ip.po.quotation.not-assigned")));
    }

    private IpPurchaseOrderOtherChargeEntity findOwnChargeById(UUID otherChargeId, UUID poId) {
        return ownChargeRepository.fetchOneById(otherChargeId, poId)
                .orElseThrow(() -> new NotExistIpPurchaseOrderException(simpleMessage("ip.po.other-charges.not-exist")));
    }

    private IpPurchaseOrderEntity findEntityById(UUID id) {
        return poRepository.findById(id).orElseThrow(
                () -> new NotExistIpPurchaseOrderException(simpleMessage("ip.po.not-exist")));
    }

    private void validateOpenPO(IpPurchaseOrderEntity entity, UserEntity userAuthenticated) {
        if (entity.getOpenBy() == null)
            throw new NotOpenIpPurchaseOrderException(simpleMessage("ip.po.not-block"));
        if (!entity.getOpenBy().getId().equals(userAuthenticated.getId()))
            throw new NotOpenIpPurchaseOrderException(
                    compositeMessage("ip.po.not-block-by", new String[]{entity.getOpenBy().getFullName()}));
    }

    private void validateEditable(IpPurchaseOrderEntity po) {
        Optional.of(po.getStatus())
                .filter(status -> !isOpenStatus(status))
                .ifPresent(status -> {
                    throw new IpPurchaseOrderNotEditableException(simpleMessage("ip.po.not-editable"));
                });
    }

    private static boolean isOpenStatus(IpPurchaseOrderStatus status) {
        return status == IpPurchaseOrderStatus.CREATED
                || status == IpPurchaseOrderStatus.SENT
                || status == IpPurchaseOrderStatus.ANSWERED;
    }
}
