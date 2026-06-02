package com.itradingsolutions.itex.api.ip.q.service.impl;

import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveDepartment;
import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveModule;
import com.itradingsolutions.itex.api.common.consecutive.services.IConsecutiveService;
import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.common.models.enums.OpenAndLockType;
import com.itradingsolutions.itex.api.common.util.IntegrityValidator;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.ip.q.exceptions.QuotationClientMismatchException;
import com.itradingsolutions.itex.api.ip.q.exceptions.NotExistIpQuotationException;
import com.itradingsolutions.itex.api.ip.q.exceptions.QuotationCurrencyMismatchException;
import com.itradingsolutions.itex.api.ip.q.exceptions.QuotationIntegrityException;
import com.itradingsolutions.itex.api.ip.q.exceptions.QuoteRequestAlreadyLinkedException;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationDTO;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationEntity;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationOtherChargeEntity;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationProductEntity;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationsClonedEntity;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationsQuoteRequestEntity;
import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationHistoryAction;
import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationStatus;
import com.itradingsolutions.itex.api.ip.q.models.filters.FilterListIpQuotation;
import com.itradingsolutions.itex.api.ip.q.models.mapper.IpQuotationMapper;
import com.itradingsolutions.itex.api.ip.q.models.mapper.IpQuotationOtherChargeMapper;
import com.itradingsolutions.itex.api.ip.q.models.requests.CreateIpQuotationRequest;
import com.itradingsolutions.itex.api.ip.q.models.requests.UpdateIpQuotationRequest;
import com.itradingsolutions.itex.api.ip.q.models.response.QuotationQuoteRequestOtherChargeResponse;
import com.itradingsolutions.itex.api.ip.q.repository.IIpQuotationClonedRepository;
import com.itradingsolutions.itex.api.ip.q.repository.IIpQuotationOtherChargeRepository;
import com.itradingsolutions.itex.api.ip.q.repository.IIpQuotationsQuoteRequestRepository;
import com.itradingsolutions.itex.api.ip.q.repository.IpQuotationRepository;
import com.itradingsolutions.itex.api.ip.q.service.IIpQuotationHistoryService;
import com.itradingsolutions.itex.api.ip.q.service.IpQuotationService;
import com.itradingsolutions.itex.api.ip.qr.exceptions.NotChangeStatusException;
import com.itradingsolutions.itex.api.ip.qr.exceptions.NotOpenQuoteRequestException;
import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestDTO;
import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestEntity;
import com.itradingsolutions.itex.api.ip.qr.models.enums.IpQuoteRequestStatus;
import com.itradingsolutions.itex.api.ip.qr.service.IIpQuoteRequestService;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientEntity;
import com.itradingsolutions.itex.api.partners.clients.repository.IClientContactRepository;
import com.itradingsolutions.itex.api.partners.clients.services.IClientContactService;
import com.itradingsolutions.itex.api.partners.clients.services.IClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class IpQuotationServiceImpl extends UtilServiceAbs implements IpQuotationService {

    private final IpQuotationRepository quotationRepository;
    private final IpQuotationMapper quotationMapper;
    private final IConsecutiveService consecutiveService;
    private final IClientService clientService;
    private final IUserService userService;
    private final IIpQuoteRequestService qrService;
    private final IIpQuotationsQuoteRequestRepository qqrRepository;
    private final IClientContactRepository clientContactRepository;
    private final IClientContactService clientContactService;
    private final IIpQuotationHistoryService historyService;
    private final IpQuotationOtherChargeMapper otherChargeMapper;
    private final IIpQuotationOtherChargeRepository otherChargeRepository;
    private final IIpQuotationClonedRepository clonedRepository;

    private static final ConsecutiveDepartment CONSECUTIVE_DEPARTMENT = ConsecutiveDepartment.IP;
    private static final ConsecutiveModule CONSECUTIVE_TYPE = ConsecutiveModule.Q;

    @Override
    @Transactional
    public void unlockIpQuotation(UUID idQuotation) {
        var quotation = findById(idQuotation);
        quotation.setOpenBy(null);
        quotation.setOpenAt(null);
        quotationRepository.save(quotation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IpQuotationDTO> listAllOpenIpQuotation(String username) {
        List<IpQuotationEntity> list = quotationRepository.fetchAllOpenByUsername(username);
        return list.stream().map(quotationMapper::entityToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<IpQuotationDTO> listAllOpenIpQuotation() {
        List<IpQuotationEntity> list = quotationRepository.fetchAllOpen();
        return list.stream().map(quotationMapper::entityToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IpQuotationDTO> listAllQuotations(Pageable pageable, FilterListIpQuotation filters) {
        Specification<IpQuotationEntity> spec = (filters == null ? Specification.where(null) : filters.filter());
        Page<IpQuotationEntity> resp = quotationRepository.findAll(spec, pageable);
        return new PageImpl<>(resp.getContent().stream().map(quotationMapper::entityToDTO).toList(), resp.getPageable(), resp.getTotalElements());
    }

    @Override
    @Transactional
    public IpQuotationDTO createQuotation(CreateIpQuotationRequest request) {
        var entity = new IpQuotationEntity();
        entity.setCurrency(request.currency());
        entity.setStatus(IpQuotationStatus.CREATED);
        entity.setCreatedAt(ZonedDateTime.now(zoneId));
        entity.setApplicationAt(ZonedDateTime.now(zoneId));
        entity.setSalesRep(userService.getUserAuthenticated());

        entity.setLeadTime(0);
        entity.setLeadTimeType(LeadTime.DAYS);
        entity.setValidity(0);
        entity.setValidityType(LeadTime.DAYS);

        var client = clientService.findClientById(request.clientId(), true);
        entity.setClient(client);
        entity.setPaymentTerms(client.getPaymentTerms());
        entity.setNumber(consecutiveService.generateConsecutive(CONSECUTIVE_TYPE, CONSECUTIVE_DEPARTMENT, client.getCode()));

        loadQuoteRequestToQuotation(request.listQrId(), entity, client);

        var resp = quotationRepository.save(entity);
        consecutiveService.saveConsecutive(CONSECUTIVE_TYPE, CONSECUTIVE_DEPARTMENT, resp.getNumber());
        var dto = quotationMapper.entityToDTO(resp);
        
        // Register CREATE history
        historyService.addHistory(IpQuotationHistoryAction.CREATE, null, dto);
        
        return dto;
    }

    @Override
    @Transactional
    public IpQuotationDTO openAndLockIpQuotation(UUID id, OpenAndLockType type) {
        var quotation = findById(id);
        if (quotation.getOpenBy() == null) {
            var user = userService.getUserAuthenticated();
            validateMaxOpenQuotations(user.getId());

            if (type.equals(OpenAndLockType.EDIT)) {
                quotation.setOpenBy(user);
                quotation.setOpenAt(ZonedDateTime.now(zoneId));
                quotation = quotationRepository.save(quotation);
            }
        }
        var dto = quotationMapper.entityToDTO(quotation);
        loadClonedByQuotation(quotation, dto);
        return dto;
    }

@Override
    @Transactional
    public IpQuotationDTO updateQuotation(UUID id, UpdateIpQuotationRequest request) {
        var quotation = findById(id);
        validateEditable(quotation);
        
        var oldDto = quotationMapper.entityToDTO(quotation);
        var oldConsecutive = quotation.getNumber();

        Optional.ofNullable(request.clientId())
            .filter(newClientId -> !newClientId.equals(quotation.getClient().getId()))
            .ifPresent(newClientId -> {
                Optional.ofNullable(quotation.getQuoteRequestsQuotations())
                    .filter(qrList -> !qrList.isEmpty())
                    .ifPresent(qrList -> {
                        throw new QuotationClientMismatchException(
                            simpleMessage("ip.q.client-change-blocked")
                        );
                    });
                
                var newClient = clientService.findClientById(newClientId, true);
                quotation.setClient(newClient);
                quotation.setPaymentTerms(newClient.getPaymentTerms());
                quotation.setNumber(consecutiveService.generateConsecutive(
                    CONSECUTIVE_TYPE, CONSECUTIVE_DEPARTMENT, newClient.getCode()
                ));
                quotation.setClientContact(null);
            });

        Optional.ofNullable(request.currency())
            .filter(newCurrency -> !newCurrency.equals(quotation.getCurrency()))
            .ifPresent(newCurrency -> {
                Optional.ofNullable(quotation.getQuoteRequestsQuotations())
                    .filter(qrList -> !qrList.isEmpty())
                    .ifPresent(qrList -> {
                        qrList.stream()
                            .filter(qr -> !qr.getQuoteRequest().getCurrency().equals(newCurrency))
                            .findFirst()
                            .ifPresent(qr -> {
                                throw new QuotationCurrencyMismatchException(
                                    compositeMessage("ip.q.currency-mismatch", new String[]{
                                        qr.getQuoteRequest().getNumber(),
                                        qr.getQuoteRequest().getCurrency().name(),
                                        newCurrency.name()
                                    })
                                );
                            });
                    });
                quotation.setCurrency(newCurrency);
            });

        if (request.clientContactId() != null) {
            quotation.setClientContact(
                clientContactService.findById(request.clientContactId(), quotation.getClient().getId())
            );
        } else {
            quotation.setClientContact(null);
        }

        if (!request.salesRepId().equals(quotation.getSalesRep().getId())) {
            quotation.setSalesRep(userService.findEntityById(request.salesRepId(), true));
        }

        quotation.setClientQNumber(request.clientQrNumber());
        quotation.setRemarks(request.remarks());
        quotation.setInternalRemarks(request.internalRemarks());

        if (request.leadTime() != null) quotation.setLeadTime(request.leadTime());
        if (request.leadTimeType() != null) quotation.setLeadTimeType(request.leadTimeType());
        if (request.validity() != null) quotation.setValidity(request.validity());
        if (request.validityType() != null) quotation.setValidityType(request.validityType());
        if (request.incoterms() != null) quotation.setIncoterms(request.incoterms());
        if (request.paymentTerms() != null) quotation.setPaymentTerms(request.paymentTerms());

        var integrityErrors = IntegrityValidator.validateQuotationIntegrity(quotation);
        if (!integrityErrors.isEmpty()) {
            throw new QuotationIntegrityException(integrityErrors);
        }

        var saved = quotationRepository.save(quotation);
        
        if (!oldConsecutive.equalsIgnoreCase(saved.getNumber())) {
            consecutiveService.saveConsecutive(CONSECUTIVE_TYPE, CONSECUTIVE_DEPARTMENT, saved.getNumber());
            consecutiveService.deleteConsecutive(CONSECUTIVE_TYPE, CONSECUTIVE_DEPARTMENT, oldConsecutive);
        }
        
        var newDto = quotationMapper.entityToDTO(saved);
        
        historyService.addHistory(IpQuotationHistoryAction.UPDATE, oldDto, newDto);
        
        return newDto;
    }

    @Override
    @Transactional
    public IpQuotationDTO changeStatusQuotation(UUID id, IpQuotationStatus newStatus) {
        return changeStatus(id, newStatus);
    }

    @Override
    @Transactional
    public IpQuotationDTO rejectQuotation(UUID id) {
        return changeStatus(id, IpQuotationStatus.REJECTED);
    }
    private IpQuotationDTO changeStatus(UUID qId, IpQuotationStatus newStatus) {
        var quotation = findById(qId);
        var oldQuotation = quotationMapper.entityToDTO(quotation);

        var currentStatus = quotation.getStatus();

        validateNotSameStatus(quotation, newStatus);
        validateStatusRequirements(quotation, newStatus);
        validateNotFromComplete(currentStatus);
        validatePurchaseOrderDependency(quotation, currentStatus, newStatus);
        validatePurchaseOrderForReject(quotation, newStatus);

        clearAllTimestamps(quotation, newStatus);
        setStatusTimestamp(quotation, newStatus);
        quotation.setStatus(newStatus);

        var newQuotation = quotationMapper.entityToDTO(quotationRepository.save(quotation));
        historyService.addHistory(IpQuotationHistoryAction.STATUS_CHANGE, oldQuotation, newQuotation);
        return newQuotation;
    }

    private void validateNotSameStatus(IpQuotationEntity quotation, IpQuotationStatus newStatus) {
        if (newStatus.equals(quotation.getStatus()))
            throw new NotChangeStatusException(simpleMessage("ip.q.equal-status"));
    }

    private void validateStatusRequirements(IpQuotationEntity quotation, IpQuotationStatus newStatus) {
        if (newStatus == IpQuotationStatus.SENT && (!quotation.isValidSent()))
            throw new NotChangeStatusException(simpleMessage("ip.q.not-valid-sent"));
        if (newStatus == IpQuotationStatus.ANSWERED && (!quotation.isValidAnswered() || quotation.getSentAt() == null))
            throw new NotChangeStatusException(simpleMessage("ip.q.not-valid-answered"));
        if (newStatus == IpQuotationStatus.COMPLETE && quotation.getAnsweredAt() == null)
            throw new NotChangeStatusException(simpleMessage("ip.q.not-valid-complete"));
    }

    private void validateNotFromComplete(IpQuotationStatus currentStatus) {
        if (currentStatus == IpQuotationStatus.COMPLETE)
            throw new NotChangeStatusException(simpleMessage("ip.q.cannot-change-complete-status"));
    }

    private void validatePurchaseOrderDependency(IpQuotationEntity quotation, IpQuotationStatus currentStatus, IpQuotationStatus newStatus) {
        if (currentStatus == IpQuotationStatus.ANSWERED &&
                (newStatus == IpQuotationStatus.SENT || newStatus == IpQuotationStatus.CREATED)) {
            validatePurchaseOrderChangeStatus(quotation);
        }
    }

    private void validatePurchaseOrderChangeStatus(IpQuotationEntity quotation) {
        //TODO, validamos que el no tenga ninguna PO asignada
        /*
        Optional.ofNullable(qr.getQuotationsQuoteRequests())
                .filter(list -> !list.isEmpty())
                .ifPresent(list -> {
                    throw new NotChangeStatusException(simpleMessage("ip.qr.assigned-to-q"));
                });

         */
    }

    private void validatePurchaseOrderForReject(IpQuotationEntity quotation, IpQuotationStatus newStatus) {
        if (newStatus != IpQuotationStatus.REJECTED) return;
        //TODO, validamos que todas las PO esten en rejected

        /*
        Optional.ofNullable(qr.getQuotationsQuoteRequests())
                .filter(list -> !list.isEmpty())
                .ifPresent(quotations -> {
                    boolean allQuotationsRejected = quotations.stream()
                            .map(IpQuotationsQuoteRequestEntity::getQuotation)
                            .map(IpQuotationEntity::getStatus)
                            .allMatch(status -> status == IpQuotationStatus.REJECTED);

                    if (!allQuotationsRejected)
                        throw new NotChangeStatusException(simpleMessage("ip.qr.assigned-to-q-rejected"));
                });
         */
    }

    private void clearAllTimestamps(IpQuotationEntity quotation, IpQuotationStatus newStatus) {
        switch (newStatus) {
            case CREATED -> {
                quotation.setSentAt(null);
                quotation.setAnsweredAt(null);
            }
            case SENT ->
                quotation.setAnsweredAt(null);

        }
    }

    private void setStatusTimestamp(IpQuotationEntity quotation, IpQuotationStatus newStatus) {
        var now = ZonedDateTime.now();
        switch (newStatus) {
            case ANSWERED -> quotation.setAnsweredAt(now);
            case SENT -> quotation.setSentAt(now);
            case COMPLETE -> quotation.setCompleteAt(now);
            case REJECTED -> quotation.setRejectAt(now);
            case CREATED -> { /* no timestamp */ }
        }
    }


    @Override
    @Transactional
    public void removeQuoteRequestFromQuotation(UUID quotationId, UUID qqrId) {
        var quotation = findById(quotationId);
        validateEditable(quotation);
        var qqr = qqrRepository.findByIdAndQuotation_Id(qqrId, quotationId)
                .orElseThrow(() -> new NotExistIpQuotationException(simpleMessage("ip.q.not-exist")));
        qqrRepository.delete(qqr);
    }

    @Override
    @Transactional
    public IpQuotationDTO addQuoteRequestsToQuotation(UUID quotationId, List<UUID> quoteRequestIds) {
        var quotation = findById(quotationId);
        validateEditable(quotation);

        // Initialize the list if it's null
        if (quotation.getQuoteRequestsQuotations() == null) {
            quotation.setQuoteRequestsQuotations(new ArrayList<>());
        }

        // Get existing QR IDs to check for duplicates
        var existingQrIds = quotation.getQuoteRequestsQuotations().stream()
                .map(qqr -> qqr.getQuoteRequest().getId())
                .toList();

        quoteRequestIds.forEach(qrId -> {
            // Check if QR is already linked
            if (existingQrIds.contains(qrId)) {
                throw new QuoteRequestAlreadyLinkedException(
                    compositeMessage("ip.q.qr.duplicate", new String[]{qrId.toString()})
                );
            }

            // Validate client and currency
            var qrEntity = qrService.findByIdAndClient(qrId, quotation.getClient().getId());
            validateQuoteRequestCurrency(qrEntity, quotation);

            // Create junction entity
            var item = new IpQuotationsQuoteRequestEntity();
            item.setQuotation(quotation);
            item.setQuoteRequest(qrEntity);
            quotation.getQuoteRequestsQuotations().add(item);
        });

        var saved = quotationRepository.save(quotation);
        return quotationMapper.entityToDTO(saved);
    }

    @Override
    @Transactional
    public IpQuotationDTO cloneQuotation(UUID id) {
        var original = findById(id);
        
        // Clone the quotation entity
        var cloned = quotationMapper.clone(original);
        cloned.setStatus(IpQuotationStatus.CREATED);
        cloned.setCreatedAt(ZonedDateTime.now(zoneId));
        cloned.setApplicationAt(ZonedDateTime.now(zoneId));
        cloned.setNumber(consecutiveService.generateConsecutive(CONSECUTIVE_TYPE, CONSECUTIVE_DEPARTMENT, original.getClient().getCode()));
        
        // Clone QRs and products
        cloned.setQuoteRequestsQuotations(new ArrayList<>());
        if (original.getQuoteRequestsQuotations() != null) {
            original.getQuoteRequestsQuotations().forEach(originalQqr -> {
                var clonedQqr = new IpQuotationsQuoteRequestEntity();
                clonedQqr.setQuotation(cloned);
                clonedQqr.setQuoteRequest(originalQqr.getQuoteRequest());
                clonedQqr.setQuotationProducts(new ArrayList<>());
                
                // Clone products
                if (originalQqr.getQuotationProducts() != null) {
                    originalQqr.getQuotationProducts().forEach(originalProduct -> {
                        var clonedProduct = new IpQuotationProductEntity();
                        clonedProduct.setQuotationsQuoteRequest(clonedQqr);
                        clonedProduct.setQuoteRequestProduct(originalProduct.getQuoteRequestProduct());
                        clonedProduct.setNumber(originalProduct.getNumber());
                        clonedProduct.setProfitMargin(originalProduct.getProfitMargin());
                        clonedProduct.setCondition(originalProduct.getCondition());
                        clonedProduct.setCreatedAt(ZonedDateTime.now(zoneId));
                        clonedQqr.getQuotationProducts().add(clonedProduct);
                    });
                }
                
                cloned.getQuoteRequestsQuotations().add(clonedQqr);
            });
        }
        
        // Clone other charges
        cloned.setOtherCharges(new ArrayList<>());
        if (original.getOtherCharges() != null && !original.getOtherCharges().isEmpty()) {
            original.getOtherCharges().forEach(originalOtherCharge -> {
                var clonedOtherCharge = otherChargeMapper.clone(originalOtherCharge);
                clonedOtherCharge.setIpQuotation(cloned);
                clonedOtherCharge.setCreatedAt(ZonedDateTime.now(zoneId));
                cloned.getOtherCharges().add(clonedOtherCharge);
            });
        }
        
        var saved = quotationRepository.save(cloned);
        consecutiveService.saveConsecutive(CONSECUTIVE_TYPE, CONSECUTIVE_DEPARTMENT, saved.getNumber());

        var clonedRelation = new IpQuotationsClonedEntity();
        clonedRelation.setId(original.getId(), saved.getId());
        clonedRelation.setMainQuotation(original);
        clonedRelation.setClonedQuotation(saved);
        clonedRepository.save(clonedRelation);

        var dto = quotationMapper.entityToDTO(saved);

        // Register CLONE history in original quotation
        var originalDto = quotationMapper.entityToDTO(original);
        historyService.addHistory(IpQuotationHistoryAction.CLONE, null, originalDto);
        
        // Register CREATE history in cloned quotation
        historyService.addHistory(IpQuotationHistoryAction.CREATE, null, dto);
        
        return dto;
    }

    private void validateEditable(IpQuotationEntity quotation) {
        if (quotation.getStatus() == IpQuotationStatus.COMPLETE || quotation.getStatus() == IpQuotationStatus.REJECTED)
            throw new NotExistIpQuotationException(simpleMessage("ip.q.not-exist"));
    }

    private void validateMaxOpenQuotations(UUID idUser) {
        if (quotationRepository.countByOpenUserId(idUser) >= maxTabsOpen)
            throw new NotOpenQuoteRequestException(compositeMessage("ip.q.not-open-max", new String[]{maxTabsOpen.toString()}));
    }

    private void loadQuoteRequestToQuotation(List<UUID> listQrId, IpQuotationEntity entity, ClientEntity client) {
        if (listQrId == null || listQrId.isEmpty()) return;
        entity.setQuoteRequestsQuotations(new ArrayList<>());

        listQrId.forEach(qrId -> {
            var qrEntity = qrService.findByIdAndClient(qrId, client.getId());
            validateQuoteRequestCurrency(qrEntity, entity);
            
            var item = new IpQuotationsQuoteRequestEntity();
            item.setQuotation(entity);
            item.setQuoteRequest(qrEntity);
            entity.getQuoteRequestsQuotations().add(item);
        });
    }

    /**
     * Validates that a Quote Request has the same currency as the Quotation.
     * 
     * @param qr the Quote Request entity to validate
     * @param quotation the Quotation entity
     * @throws QuotationCurrencyMismatchException if currencies don't match
     */
    private void validateQuoteRequestCurrency(com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestEntity qr, IpQuotationEntity quotation) {
        if (!qr.getCurrency().equals(quotation.getCurrency())) {
            throw new QuotationCurrencyMismatchException(
                compositeMessage("ip.q.currency-mismatch", new String[]{qr.getNumber(), qr.getCurrency().name(), quotation.getCurrency().name()})
            );
        }
    }

    private IpQuotationEntity findById(UUID id) {
        return quotationRepository.findById(id).orElseThrow(() ->
                new NotExistIpQuotationException(simpleMessage("ip.q.not-exist"))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public IpQuotationDTO getQuotationForHistory(UUID id) {
        var entity = findById(id);
        return quotationMapper.entityToDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public IpQuotationEntity getEntityById(UUID id) {
        return findById(id);
    }

    @Override
    public void validateQuotationInCreatedStatus(IpQuotationEntity entity, com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity user) {
        if (entity.getStatus() != IpQuotationStatus.CREATED) {
            throw new NotOpenQuoteRequestException(simpleMessage("ip.q.not-created-status"));
        }
    }

    /**
     * Unlocks all currently open Quotations by clearing openBy and openAt fields.
     * Called by scheduler to prevent Quotations from being locked indefinitely.
     */
    @Override
    @Transactional
    public void unlockAllOpenQuotations() {
        var openQuotations = quotationRepository.fetchAllOpen();
        
        openQuotations.forEach(quotation -> {
            quotation.setOpenBy(null);
            quotation.setOpenAt(null);
            quotationRepository.save(quotation);
        });
    }

    /**
     * Auto-rejects CREATED Quotations older than 45 days.
     * Called by scheduler to maintain data hygiene.
     */
    @Override
    @Transactional
    public void autoRejectOldCreatedQuotations() {
        var cutoffDate = ZonedDateTime.now().minusDays(45);
        
        var oldQuotations = quotationRepository.findAll().stream()
                .filter(q -> q.getStatus() == IpQuotationStatus.CREATED)
                .filter(q -> q.getCreatedAt().isBefore(cutoffDate))
                .toList();
        
        oldQuotations.forEach(quotation -> {
            quotation.setStatus(IpQuotationStatus.REJECTED);
            quotation.setRejectAt(ZonedDateTime.now());
            quotationRepository.save(quotation);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuotationQuoteRequestOtherChargeResponse> getOtherChargesFromQuoteRequests(UUID quotationId) {
        var quotation = findById(quotationId);

        if (quotation.getQuoteRequestsQuotations() == null) {
            return List.of();
        }

        List<QuotationQuoteRequestOtherChargeResponse> result = new ArrayList<>();

        quotation.getQuoteRequestsQuotations().forEach(qqr -> {
            var qr = qqr.getQuoteRequest();
            if (qr != null && qr.getOtherCharges() != null) {
                qr.getOtherCharges().forEach(charge -> {
                    result.add(new QuotationQuoteRequestOtherChargeResponse(
                            charge.getDescription(),
                            charge.getValue(),
                            qr.getNumber()
                    ));
                });
            }
        });

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> validateIntegrity(UUID quotationId) {
        var quotation = findById(quotationId);
        return IntegrityValidator.validateQuotationIntegrity(quotation);
    }

    private void loadClonedByQuotation(IpQuotationEntity entity, IpQuotationDTO dto) {
        clonedRepository.fetchByClonedId(entity.getId())
                .ifPresent(cloned -> dto.setClonedByQuotation(
                        quotationMapper.entityToDTO(cloned.getMainQuotation())
                ));
    }
}
