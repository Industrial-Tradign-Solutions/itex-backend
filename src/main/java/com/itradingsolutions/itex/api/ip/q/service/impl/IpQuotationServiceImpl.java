package com.itradingsolutions.itex.api.ip.q.service.impl;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveDepartment;
import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveModule;
import com.itradingsolutions.itex.api.common.consecutive.services.IConsecutiveService;
import com.itradingsolutions.itex.api.common.jasper.exceptions.NotGenerateReportException;
import com.itradingsolutions.itex.api.common.jasper.model.enums.JasperReport;
import com.itradingsolutions.itex.api.common.jasper.service.JasperService;
import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.common.models.enums.OpenAndLockType;
import com.itradingsolutions.itex.api.common.util.IntegrityValidator;
import com.itradingsolutions.itex.api.common.util.models.StatusTransition;
import com.itradingsolutions.itex.api.common.util.models.TransitionKey;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.common.util.models.enums.Language;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.ip.po.repository.IIpPurchaseOrderRepository;
import com.itradingsolutions.itex.api.ip.products.models.enums.IpProductStatus;
import com.itradingsolutions.itex.api.ip.q.models.dto.QuotationProductStatusProjection;
import com.itradingsolutions.itex.api.ip.q.models.dto.reports.IpQuotationReportDTO;
import com.itradingsolutions.itex.api.ip.q.exceptions.QuotationClientMismatchException;
import com.itradingsolutions.itex.api.ip.q.exceptions.NotExistIpQuotationException;
import com.itradingsolutions.itex.api.ip.q.exceptions.QuotationCurrencyMismatchException;
import com.itradingsolutions.itex.api.ip.q.exceptions.QuotationIntegrityException;
import com.itradingsolutions.itex.api.ip.q.exceptions.QuotationStatusRestrictionException;
import com.itradingsolutions.itex.api.ip.q.exceptions.QuoteRequestAlreadyLinkedException;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationDTO;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationEntity;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationOtherChargeEntity;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationOtherChargesQuoteRequestEntity;
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
import com.itradingsolutions.itex.api.ip.q.models.response.AvailableForPurchaseOrderResponse;
import com.itradingsolutions.itex.api.ip.q.models.response.ListIpQuotationResponse;
import com.itradingsolutions.itex.api.ip.q.models.response.QuotationQuoteRequestOtherChargeResponse;
import com.itradingsolutions.itex.api.ip.q.repository.IIpQuotationClonedRepository;
import com.itradingsolutions.itex.api.ip.q.repository.IIpQuotationOtherChargeRepository;
import com.itradingsolutions.itex.api.ip.q.repository.IIpQuotationProductRepository;
import com.itradingsolutions.itex.api.ip.q.repository.IIpQuotationsQuoteRequestRepository;
import com.itradingsolutions.itex.api.ip.q.repository.IpQuotationRepository;
import com.itradingsolutions.itex.api.ip.q.service.IIpQuotationHistoryService;
import com.itradingsolutions.itex.api.ip.q.service.IpQuotationService;
import com.itradingsolutions.itex.api.ip.qr.exceptions.NotChangeStatusException;
import com.itradingsolutions.itex.api.ip.qr.exceptions.NotOpenQuoteRequestException;
import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestDTO;
import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestEntity;
import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestProductEntity;
import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierEntity;
import com.itradingsolutions.itex.api.partners.suppliers.models.mappers.SupplierMapper;
import com.itradingsolutions.itex.api.partners.suppliers.models.responses.BasicSupplierResponse;
import com.itradingsolutions.itex.api.ip.qr.models.enums.IpQuoteRequestHistoryAction;
import com.itradingsolutions.itex.api.ip.qr.models.enums.IpQuoteRequestStatus;
import com.itradingsolutions.itex.api.ip.qr.service.IIpQuoteRequestHistoryService;
import com.itradingsolutions.itex.api.ip.qr.service.IIpQuoteRequestService;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientEntity;
import com.itradingsolutions.itex.api.partners.clients.repository.IClientContactRepository;
import com.itradingsolutions.itex.api.partners.clients.services.IClientContactService;
import com.itradingsolutions.itex.api.partners.clients.services.IClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jasperreports.engine.JRException;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
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
    private final IIpQuoteRequestHistoryService qrHistoryService;
    private final IIpQuotationHistoryService historyService;
    private final IpQuotationOtherChargeMapper otherChargeMapper;
    private final IIpQuotationOtherChargeRepository otherChargeRepository;
    private final IIpQuotationClonedRepository clonedRepository;
    private final JasperService jasperService;
    private final SupplierMapper supplierMapper;
    private final IIpPurchaseOrderRepository purchaseOrderRepository;
    private final IIpQuotationProductRepository qProductRepository;

    private static final ConsecutiveDepartment CONSECUTIVE_DEPARTMENT = ConsecutiveDepartment.IP;
    private static final ConsecutiveModule CONSECUTIVE_TYPE = ConsecutiveModule.Q;
    private static final ZoneId QUOTATION_ZONE_ID = ZoneId.of("America/New_York");
    private static final int AUTO_REJECT_DAYS = 30;

    private static final Predicate<IpQuotationEntity> HAS_QUOTE_REQUESTS =
            quotation -> Optional.ofNullable(quotation.getQuoteRequestsQuotations())
                    .filter(quoteRequests -> !quoteRequests.isEmpty())
                    .isPresent();

    private static final Predicate<IpQuotationEntity> HAS_PRODUCTS =
            quotation -> Optional.ofNullable(quotation.getQuoteRequestsQuotations())
                    .stream()
                    .flatMap(List::stream)
                    .map(IpQuotationsQuoteRequestEntity::getQuotationProducts)
                    .filter(Objects::nonNull)
                    .flatMap(List::stream)
                    .findAny()
                    .isPresent();

    private static final Consumer<IpQuotationEntity> STAMP_REJECT =
            quotation -> quotation.setRejectAt(ZonedDateTime.now(QUOTATION_ZONE_ID));

    private static final Map<TransitionKey<IpQuotationStatus>, StatusTransition<IpQuotationEntity>> TRANSITIONS = Map.ofEntries(
            Map.entry(new TransitionKey<>(IpQuotationStatus.CREATED, IpQuotationStatus.SENT),
                    new StatusTransition<>(HAS_QUOTE_REQUESTS.and(HAS_PRODUCTS),
                            "ip.q.not-valid-sent", quotation -> quotation.setSentAt(ZonedDateTime.now(QUOTATION_ZONE_ID)))),
            Map.entry(new TransitionKey<>(IpQuotationStatus.SENT, IpQuotationStatus.ANSWERED),
                    new StatusTransition<>(HAS_PRODUCTS.and(quotation -> quotation.getSentAt() != null),
                            "ip.q.not-valid-answered", quotation -> quotation.setAnsweredAt(ZonedDateTime.now(QUOTATION_ZONE_ID)))),
            Map.entry(new TransitionKey<>(IpQuotationStatus.ANSWERED, IpQuotationStatus.COMPLETE),
                    new StatusTransition<>(HAS_PRODUCTS.and(quotation -> quotation.getAnsweredAt() != null),
                            "ip.q.not-valid-complete", quotation -> quotation.setCompleteAt(ZonedDateTime.now(QUOTATION_ZONE_ID)))),
            Map.entry(new TransitionKey<>(IpQuotationStatus.ANSWERED, IpQuotationStatus.SENT),
                    StatusTransition.unrestricted(quotation -> quotation.setAnsweredAt(null))),
            Map.entry(new TransitionKey<>(IpQuotationStatus.ANSWERED, IpQuotationStatus.CREATED),
                    StatusTransition.unrestricted(quotation -> {
                        quotation.setAnsweredAt(null);
                        quotation.setSentAt(null);
                    })),
            Map.entry(new TransitionKey<>(IpQuotationStatus.SENT, IpQuotationStatus.CREATED),
                    StatusTransition.unrestricted(quotation -> quotation.setSentAt(null))),
            Map.entry(new TransitionKey<>(IpQuotationStatus.CREATED, IpQuotationStatus.REJECTED),
                    StatusTransition.unrestricted(STAMP_REJECT)),
            Map.entry(new TransitionKey<>(IpQuotationStatus.SENT, IpQuotationStatus.REJECTED),
                    StatusTransition.unrestricted(STAMP_REJECT)),
            Map.entry(new TransitionKey<>(IpQuotationStatus.ANSWERED, IpQuotationStatus.REJECTED),
                    StatusTransition.unrestricted(STAMP_REJECT))
    );

    private static final Map<IpQuotationStatus, String> TERMINAL_STATUS_KEYS = Map.of(
            IpQuotationStatus.COMPLETE, "ip.q.cannot-change-complete-status",
            IpQuotationStatus.REJECTED, "ip.q.cannot-change-rejected-status"
    );

    /**
     * Advance transitions that require the Incoterm to be defined on the Quotation.
     * Rollbacks targeting SENT are intentionally excluded to avoid blocking legacy data.
     */
    private static final Set<TransitionKey<IpQuotationStatus>> INCOTERMS_REQUIRED_TRANSITIONS = Set.of(
            new TransitionKey<>(IpQuotationStatus.CREATED, IpQuotationStatus.SENT),
            new TransitionKey<>(IpQuotationStatus.SENT, IpQuotationStatus.ANSWERED),
            new TransitionKey<>(IpQuotationStatus.ANSWERED, IpQuotationStatus.COMPLETE)
    );

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
    public Page<ListIpQuotationResponse> listAllQuotations(Pageable pageable, FilterListIpQuotation filters) {
        Specification<IpQuotationEntity> spec = (filters == null ? Specification.where(null) : filters.filter());
        Page<IpQuotationEntity> resp = quotationRepository.findAll(spec, pageable);
        return new PageImpl<>(resp.getContent().stream().map(quotationMapper::entityToListResponse).toList(), resp.getPageable(), resp.getTotalElements());
    }

    @Override
    @Transactional
    public IpQuotationDTO createQuotation(CreateIpQuotationRequest request) {
        var entity = new IpQuotationEntity();
        entity.setCurrency(request.currency());
        entity.setStatus(IpQuotationStatus.CREATED);
        entity.setCreatedAt(ZonedDateTime.now(zoneId));
        entity.setApplicationAt(request.applicationAt());
        entity.setSalesRep(userService.getUserAuthenticated());

        entity.setLeadTime(0);
        entity.setLeadTimeType(LeadTime.DAYS);
        entity.setValidity(0);
        entity.setValidityType(LeadTime.DAYS);
        entity.setProfitMarginFreightCharges(BigDecimal.ZERO);
        entity.setFreightChargeMiamiITS(BigDecimal.ZERO);

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
        return toDto(quotation);
    }

    @Override
    @Transactional
    public IpQuotationDTO updateQuotation(UUID id, UpdateIpQuotationRequest request) {
        var quotation = findById(id);
        var user = userService.getUserAuthenticated();
        validateQuotationEditable(quotation, user);
        
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
        if (validateAction(user, ModuleAction.EDIT_PAYMENT_TERMS_IP_QUOTATIONS) && request.paymentTerms() != null)
            quotation.setPaymentTerms(request.paymentTerms());

        Optional.ofNullable(request.profitMarginFreightCharges())
                .ifPresent(quotation::setProfitMarginFreightCharges);
        Optional.ofNullable(request.freightChargeMiamiITS())
                .ifPresent(quotation::setFreightChargeMiamiITS);

        if (request.applicationAt() != null
                && !request.applicationAt().equals(quotation.getApplicationAt())) {
            quotation.setApplicationAt(request.applicationAt());
        }

        var integrityErrors = IntegrityValidator.validateQuotationIntegrity(quotation);
        if (!integrityErrors.isEmpty()) {
            throw new QuotationIntegrityException(integrityErrors);
        }

        var saved = quotationRepository.save(quotation);
        
        if (!oldConsecutive.equalsIgnoreCase(saved.getNumber())) {
            consecutiveService.saveConsecutive(CONSECUTIVE_TYPE, CONSECUTIVE_DEPARTMENT, saved.getNumber());
            consecutiveService.deleteConsecutive(CONSECUTIVE_TYPE, CONSECUTIVE_DEPARTMENT, oldConsecutive);
        }
        
        var newDto = toDto(saved);

        if (!isSameAmount(oldDto.getProfitMarginFreightCharges(), newDto.getProfitMarginFreightCharges())
                || !isSameAmount(oldDto.getFreightChargeMiamiITS(), newDto.getFreightChargeMiamiITS())) {
            log.info("Quotation {} freight values updated: profitMarginFreightCharges [{} -> {}], freightChargeMiamiITS [{} -> {}]",
                    saved.getNumber(),
                    oldDto.getProfitMarginFreightCharges(), newDto.getProfitMarginFreightCharges(),
                    oldDto.getFreightChargeMiamiITS(), newDto.getFreightChargeMiamiITS());
        }

        historyService.addHistory(IpQuotationHistoryAction.UPDATE, oldDto, newDto);
        
        return newDto;
    }

    private static boolean isSameAmount(BigDecimal first, BigDecimal second) {
        if (first == null || second == null) {
            return first == second;
        }
        return first.compareTo(second) == 0;
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
        validateTerminalStatus(currentStatus);
        validateApplicationAtForStatus(quotation, newStatus);
        validateIncotermsForStatus(quotation, currentStatus, newStatus);
        validatePurchaseOrderDependency(quotation, currentStatus, newStatus);
        validatePurchaseOrderForReject(quotation, newStatus);
        validatePurchaseOrderForComplete(quotation, newStatus);
        validateQuoteRequestsForReject(quotation, newStatus);
        validateManualComplete(currentStatus, newStatus);
        validateProductsActiveForStatusChange(quotation, newStatus);

        var transition = resolveTransition(currentStatus, newStatus);
        validateRequirement(transition, quotation);
        transition.sideEffect().accept(quotation);
        quotation.setStatus(newStatus);
        clearOpenLockOnFinalStatus(quotation);

        var savedQuotation = quotationRepository.save(quotation);
        log.info("Quotation {} ({}) status changed from {} to {}",
                savedQuotation.getNumber(), savedQuotation.getId(), currentStatus, newStatus);
        if (newStatus == IpQuotationStatus.ANSWERED) {
            processQuoteRequestsOnAnswered(savedQuotation);
        }

        var newQuotation = toDto(savedQuotation);
        historyService.addHistory(IpQuotationHistoryAction.STATUS_CHANGE, oldQuotation, newQuotation);
        return newQuotation;
    }

    /**
     * When a Quotation is ANSWERED, each linked Quote Request is transitioned automatically:
     * QRs contributing at least one product to the Quotation are marked COMPLETE, while
     * QRs with no products in the Quotation are marked REJECTED (this automatic flow is
     * allowed to reject QRs even while linked to a Quotation, unlike the manual flow).
     * Other Charges imported from a QR do not influence this decision.
     */
    private void processQuoteRequestsOnAnswered(IpQuotationEntity quotation) {
        var qqrList = qqrRepository.findByQuotationIdWithQuoteRequestsAndProducts(quotation.getId());
        if (qqrList.isEmpty()) return;

        var user = userService.getUserAuthenticated();

        for (var qqr : qqrList) {
            var qr = qqr.getQuoteRequest();
            if (qr == null) continue;

            boolean hasProductsInQuotation = qqr.getQuotationProducts() != null
                    && !qqr.getQuotationProducts().isEmpty();

            var targetStatus = hasProductsInQuotation
                    ? IpQuoteRequestStatus.COMPLETE
                    : IpQuoteRequestStatus.REJECTED;

            var oldStatus = qr.getStatus();
            qrService.changeStatusInternal(qr.getId(), targetStatus);

            qrHistoryService.addHistoryAutoStatusChange(
                    IpQuoteRequestHistoryAction.STATUS_CHANGE_BY_Q,
                    qr.getId(),
                    oldStatus,
                    targetStatus,
                    quotation.getNumber(),
                    user
            );
        }
    }

    private void validateNotSameStatus(IpQuotationEntity quotation, IpQuotationStatus newStatus) {
        Optional.of(newStatus)
                .filter(status -> status == quotation.getStatus())
                .ifPresent(status -> {
                    throw new NotChangeStatusException(simpleMessage("ip.q.equal-status"));
                });
    }

    private void validateTerminalStatus(IpQuotationStatus currentStatus) {
        Optional.ofNullable(TERMINAL_STATUS_KEYS.get(currentStatus))
                .ifPresent(messageKey -> {
                    throw new NotChangeStatusException(simpleMessage(messageKey));
                });
    }

    private void validateApplicationAtForStatus(IpQuotationEntity quotation, IpQuotationStatus newStatus) {
        if ((newStatus == IpQuotationStatus.SENT || newStatus == IpQuotationStatus.ANSWERED)
                && quotation.getApplicationAt() == null) {
            throw new NotChangeStatusException(simpleMessage("ip.q.application-at-required"));
        }
    }

    /**
     * The Incoterm is mandatory to advance the Quotation to SENT, ANSWERED or COMPLETE.
     * Rollbacks targeting SENT (e.g. ANSWERED → SENT) are not validated, so legacy
     * Quotations without an Incoterm can still be reverted.
     */
    private void validateIncotermsForStatus(IpQuotationEntity quotation, IpQuotationStatus currentStatus,
                                            IpQuotationStatus newStatus) {
        var transition = new TransitionKey<>(currentStatus, newStatus);
        if (!INCOTERMS_REQUIRED_TRANSITIONS.contains(transition)) {
            return;
        }

        if (quotation.getIncoterms() == null) {
            log.warn("Quotation {} ({}) cannot transition from {} to {}: incoterms are required",
                    quotation.getNumber(), quotation.getId(), currentStatus, newStatus);
            throw new NotChangeStatusException(simpleMessage("ip.q.incoterms-required"));
        }
    }

    private StatusTransition<IpQuotationEntity> resolveTransition(IpQuotationStatus currentStatus,
                                                                  IpQuotationStatus newStatus) {
        return Optional.ofNullable(TRANSITIONS.get(new TransitionKey<>(currentStatus, newStatus)))
                .orElseThrow(() -> new NotChangeStatusException(
                        compositeMessage("ip.q.invalid-transition",
                                new String[]{currentStatus.name(), newStatus.name()})));
    }

    private void validateRequirement(StatusTransition<IpQuotationEntity> transition, IpQuotationEntity quotation) {
        Optional.of(transition)
                .filter(item -> !item.requirement().test(quotation))
                .ifPresent(item -> {
                    throw new NotChangeStatusException(simpleMessage(item.requirementErrorKey()));
                });
    }

    private void validatePurchaseOrderDependency(IpQuotationEntity quotation, IpQuotationStatus currentStatus, IpQuotationStatus newStatus) {
        if (currentStatus == IpQuotationStatus.ANSWERED &&
                (newStatus == IpQuotationStatus.SENT || newStatus == IpQuotationStatus.CREATED)) {
            validatePurchaseOrderChangeStatus(quotation);
        }
    }

    private void validatePurchaseOrderChangeStatus(IpQuotationEntity quotation) {
        if (purchaseOrderRepository.existsByQuotation_Id(quotation.getId())) {
            throw new QuotationStatusRestrictionException(simpleMessage("ip.q.cannot-revert-with-po"));
        }
    }

    /**
     * A Quotation can only be manually REJECTED when it has no Purchase Orders associated,
     * or when every associated Purchase Order is already REJECTED.
     */
    private void validatePurchaseOrderForReject(IpQuotationEntity quotation, IpQuotationStatus newStatus) {
        if (newStatus != IpQuotationStatus.REJECTED) return;

        var poStatuses = purchaseOrderRepository.fetchStatusesByQuotationId(quotation.getId());
        if (poStatuses.isEmpty()) return;

        var allRejected = poStatuses.stream()
                .allMatch(status -> status == com.itradingsolutions.itex.api.ip.po.models.enums.IpPurchaseOrderStatus.REJECTED);
        if (!allRejected) {
            throw new QuotationStatusRestrictionException(simpleMessage("ip.q.po-not-all-rejected"));
        }
    }

    /**
     * A Quotation can only be COMPLETED when it has at least one Purchase Order associated.
     */
    private void validatePurchaseOrderForComplete(IpQuotationEntity quotation, IpQuotationStatus newStatus) {
        if (newStatus != IpQuotationStatus.COMPLETE) return;
        if (!purchaseOrderRepository.existsByQuotation_Id(quotation.getId())) {
            throw new QuotationStatusRestrictionException(simpleMessage("ip.q.complete-requires-po"));
        }
    }

    /**
     * A Quotation can only be manually REJECTED when every linked Quote Request is COMPLETE or REJECTED.
     */
    private void validateQuoteRequestsForReject(IpQuotationEntity quotation, IpQuotationStatus newStatus) {
        if (newStatus != IpQuotationStatus.REJECTED) return;

        var allowedStatuses = List.of(IpQuoteRequestStatus.COMPLETE, IpQuoteRequestStatus.REJECTED);
        var countNotAllowed = qqrRepository.countQuoteRequestsNotInStatuses(quotation.getId(), allowedStatuses);
        if (countNotAllowed > 0) {
            throw new QuotationStatusRestrictionException(simpleMessage("ip.q.qr-not-completed-or-rejected"));
        }
    }

    /**
     * Manual completion is only allowed from ANSWERED and requires the COMPLETE_IP_QUOTATIONS permission.
     */
    private void validateManualComplete(IpQuotationStatus currentStatus, IpQuotationStatus newStatus) {
        if (newStatus != IpQuotationStatus.COMPLETE) return;

        var user = userService.getUserAuthenticated();
        if (!validateAction(user, ModuleAction.COMPLETE_IP_QUOTATIONS)) {
            throw new NotChangeStatusException(simpleMessage("ip.q.no-manual-complete"));
        }
        if (currentStatus != IpQuotationStatus.ANSWERED) {
            throw new NotChangeStatusException(simpleMessage("ip.q.manual-complete-requires-answered"));
        }
    }

    /**
     * All products in the Quotation must be ACTIVE for any status change except REJECTED.
     */
    private void validateProductsActiveForStatusChange(IpQuotationEntity quotation, IpQuotationStatus newStatus) {
        if (newStatus == IpQuotationStatus.REJECTED) return;

        var notActiveProducts = qProductRepository.fetchProductsNotInStatus(quotation.getId(), IpProductStatus.ACTIVE);
        if (!notActiveProducts.isEmpty()) {
            throw new NotChangeStatusException(compositeMessage(
                    "ip.q.product.not-active",
                    new String[]{formatNotActiveProducts(notActiveProducts)}
            ));
        }
    }

    private static String formatNotActiveProducts(List<QuotationProductStatusProjection> notActiveProducts) {
        return notActiveProducts.stream()
                .map(projection -> {
                    var mfrReference = projection.mfrReference();
                    var description = projection.description();
                    var status = projection.status().name();
                    var label = mfrReference != null && !mfrReference.isBlank() ? mfrReference : description;
                    return label + " (" + status + ")";
                })
                .collect(java.util.stream.Collectors.joining(", "));
    }

    @Override
    @Transactional
    public void removeQuoteRequestFromQuotation(UUID quotationId, UUID qqrId) {
        var quotation = findById(quotationId);
        validateQuoteRequestModificationAllowed(quotation, "ip.q.qr.cannot-delete");
        var exists = qqrRepository.existsByIdAndQuotation_Id(qqrId, quotationId);
        if (!exists) {
            throw new NotExistIpQuotationException(simpleMessage("ip.q.qr.not-exist"));
        }
        qqrRepository.deleteProductsByQqrId(qqrId);
        qqrRepository.deleteOtherChargesByQqrId(qqrId);
        qqrRepository.deleteQqrById(qqrId);
    }

    @Override
    @Transactional
    public IpQuotationDTO addQuoteRequestsToQuotation(UUID quotationId, List<UUID> quoteRequestIds) {
        var quotation = findById(quotationId);
        validateQuoteRequestModificationAllowed(quotation, "ip.q.qr.cannot-add");

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
        return toDto(saved);
    }

    @Override
    @Transactional
    public IpQuotationDTO cloneQuotation(UUID id) {
        var original = findById(id);
        var user = userService.getUserAuthenticated();
        if (isOpenStatus(original.getStatus()))
            validateOpenQuotation(original, user);
        validateMaxOpenQuotations(user.getId());

        // Clone the quotation entity
        var cloned = quotationMapper.clone(original);
        cloned.setStatus(IpQuotationStatus.CREATED);
        cloned.setCreatedAt(ZonedDateTime.now(zoneId));
        cloned.setApplicationAt(null);
        cloned.setProfitMarginFreightCharges(
                Optional.ofNullable(cloned.getProfitMarginFreightCharges()).orElse(BigDecimal.ZERO));
        cloned.setFreightChargeMiamiITS(
                Optional.ofNullable(cloned.getFreightChargeMiamiITS()).orElse(BigDecimal.ZERO));
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

                // Clone QR-imported other charges
                clonedQqr.setQuotationsOtherCharges(new ArrayList<>());
                if (originalQqr.getQuotationsOtherCharges() != null) {
                    originalQqr.getQuotationsOtherCharges().forEach(originalImported -> {
                        var clonedImported = new IpQuotationOtherChargesQuoteRequestEntity();
                        clonedImported.setQuotationsQuoteRequest(clonedQqr);
                        clonedImported.setQrOtherCharge(originalImported.getQrOtherCharge());
                        clonedImported.setCreatedAt(ZonedDateTime.now(zoneId));
                        clonedQqr.getQuotationsOtherCharges().add(clonedImported);
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

        return quotationMapper.entityToDTO(saved);
    }

    /**
     * Quote Requests can only be added to or removed from a Quotation while it is in
     * CREATED status. In any other status the linked QRs become immutable; the Quotation
     * must be moved back to CREATED first in order to modify its Quote Requests.
     */
    private void validateQuoteRequestModificationAllowed(IpQuotationEntity quotation, String messageKey) {
        if (isFinalStatus(quotation.getStatus())) {
            throw new QuotationStatusRestrictionException(simpleMessage("ip.q.not-editable-by-status"));
        }
        if (quotation.getStatus() != IpQuotationStatus.CREATED) {
            throw new QuotationStatusRestrictionException(simpleMessage(messageKey));
        }
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
    @Transactional(readOnly = true)
    public List<AvailableForPurchaseOrderResponse> getAvailableForPurchaseOrder(UUID clientId, boolean viewCompleted, Currency currency) {
        var statuses = new ArrayList<IpQuotationStatus>();
        statuses.add(IpQuotationStatus.ANSWERED);
        if (viewCompleted) statuses.add(IpQuotationStatus.COMPLETE);

        var quotations = quotationRepository.fetchByClientAndStatus(clientId, statuses, currency);
        if (quotations.isEmpty()) {
            return List.of();
        }

        var quotationIds = quotations.stream()
                .map(IpQuotationEntity::getId)
                .toList();
        var productsByQuotationId = qProductRepository.fetchByQuotationIds(quotationIds).stream()
                .collect(Collectors.groupingBy(qp -> qp.getQuotationsQuoteRequest().getQuotation().getId()));

        return quotations.stream()
                .map(quotation -> toAvailableForPurchaseOrderResponse(quotation, productsByQuotationId.getOrDefault(quotation.getId(), List.of())))
                .toList();
    }

    private AvailableForPurchaseOrderResponse toAvailableForPurchaseOrderResponse(IpQuotationEntity entity,
                                                                                  List<IpQuotationProductEntity> products) {
        var suppliers = products.stream()
                .map(IpQuotationProductEntity::getQuoteRequestProduct)
                .filter(qrp -> qrp != null)
                .map(IpQuoteRequestProductEntity::getIpQuoteRequest)
                .filter(qr -> qr != null)
                .map(IpQuoteRequestEntity::getSupplier)
                .filter(s -> s != null)
                .distinct()
                .map(supplierMapper::entityToDto)
                .map(supplierMapper::dtoToBasicResponse)
                .toList();

        return new AvailableForPurchaseOrderResponse(
                entity.getId(),
                entity.getNumber(),
                entity.getNumber(),
                entity.getStatus(),
                entity.getApplicationAt(),
                suppliers
        );
    }

    @Override
    public void validateQuotationEditable(IpQuotationEntity entity, com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity user) {
        if (isFinalStatus(entity.getStatus()))
            throw new QuotationStatusRestrictionException(simpleMessage("ip.q.not-editable-by-status"));
        validateOpenQuotation(entity, user);
    }

    /**
     * Internal status change used by automated flows (scheduler, QR auto-completion).
     * Does not enforce manual business validations and does not write history itself.
     */
    @Override
    @Transactional
    public void changeStatusInternal(UUID qId, IpQuotationStatus newStatus) {
        var quotation = findById(qId);
        var currentStatus = quotation.getStatus();

        if (currentStatus == newStatus || isFinalStatus(currentStatus)) {
            return;
        }

        setStatusTimestamp(quotation, newStatus);
        if (newStatus.ordinal() < currentStatus.ordinal()) {
            clearFutureTimestamps(quotation, newStatus);
        }
        quotation.setStatus(newStatus);
        clearOpenLockOnFinalStatus(quotation);
        quotationRepository.save(quotation);
    }

    private void setStatusTimestamp(IpQuotationEntity quotation, IpQuotationStatus newStatus) {
        var now = ZonedDateTime.now(zoneId);
        switch (newStatus) {
            case SENT -> quotation.setSentAt(now);
            case ANSWERED -> quotation.setAnsweredAt(now);
            case COMPLETE -> quotation.setCompleteAt(now);
            case REJECTED -> quotation.setRejectAt(now);
            case CREATED -> { /* no timestamp */ }
        }
    }

    private void clearFutureTimestamps(IpQuotationEntity quotation, IpQuotationStatus newStatus) {
        switch (newStatus) {
            case CREATED -> {
                quotation.setSentAt(null);
                quotation.setAnsweredAt(null);
            }
            case SENT -> quotation.setAnsweredAt(null);
        }
    }

    /**
     * Unlocks all currently open Quotations by clearing openBy and openAt fields.
     * Called by scheduler to prevent Quotations from being locked indefinitely.
     */
    @Override
    @Transactional
    public void unlockAllOpenQuotations() {
        quotationRepository.clearAllOpenLocks();
    }

    /**
     * Auto-rejects Quotations that have been in CREATED, SENT or ANSWERED status for more than 30 days.
     * Called by scheduler to maintain data hygiene.
     */
    @Override
    @Transactional
    public int autoRejectStaleQuotations() {
        var cutoff = LocalDate.now(zoneId).minusDays(AUTO_REJECT_DAYS - 1L).atStartOfDay(zoneId);

        var staleCreated = quotationRepository.fetchStaleCreated(IpQuotationStatus.CREATED, cutoff);
        var staleSent = quotationRepository.fetchStaleSent(IpQuotationStatus.SENT, cutoff);
        var staleAnswered = filterAnsweredRejectable(quotationRepository.fetchStaleAnswered(IpQuotationStatus.ANSWERED, cutoff));

        return rejectStale(staleCreated) + rejectStale(staleSent) + rejectStale(staleAnswered);
    }

    private List<IpQuotationEntity> filterAnsweredRejectable(List<IpQuotationEntity> quotations) {
        if (quotations.isEmpty()) {
            return List.of();
        }

        var quotationIds = quotations.stream()
                .map(IpQuotationEntity::getId)
                .toList();
        var idsWithNonRejectedPos = purchaseOrderRepository.findQuotationIdsWithNonRejectedPurchaseOrders(
                quotationIds,
                com.itradingsolutions.itex.api.ip.po.models.enums.IpPurchaseOrderStatus.REJECTED
        );

        return quotations.stream()
                .filter(quotation -> !idsWithNonRejectedPos.contains(quotation.getId()))
                .toList();
    }

    private int rejectStale(List<IpQuotationEntity> staleQuotations) {
        staleQuotations.forEach(this::rejectStaleExpired);
        return staleQuotations.size();
    }

    private void rejectStaleExpired(IpQuotationEntity quotation) {
        var oldStatus = quotation.getStatus();
        changeStatusInternal(quotation.getId(), IpQuotationStatus.REJECTED);
        historyService.addHistoryAutoStatusChange(
                IpQuotationHistoryAction.AUTO_REJECTED_TIME,
                quotation.getId(),
                oldStatus,
                IpQuotationStatus.REJECTED,
                quotation.getSalesRep()
        );
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

    @Override
    @Transactional
    public byte[] printQuotation(UUID quotationId) {
        IpQuotationEntity quotation = findById(quotationId);
        IpQuotationStatus status = quotation.getStatus();

        if (isOpenStatus(status))
            validateOpenQuotation(quotation, userService.getUserAuthenticated());

        if (quotation.getQuoteRequestsQuotations() == null || quotation.getQuoteRequestsQuotations().isEmpty())
            throw new NotGenerateReportException(simpleMessage("ip.q.not-generate-doc"));

        try {
            String pdfPath = quotation.getPdfUrl();
            if (isFinalStatus(status) && pdfPath != null)
                return jasperService.getPdfBytes(pdfPath);

            var quotationDto = quotationMapper.entityToDTO(quotation);
            log.info("Generating PDF for Quotation {}: qrFreightCharges={}, profitMarginFreightCharges={}, totalFreightCharges={}, freightChargeMiamiITS={}, total={}",
                    quotation.getNumber(), quotationDto.getFreightCharges(), quotationDto.getProfitMarginFreightCharges(),
                    quotationDto.getTotalFreightCharges(), quotationDto.getFreightChargeMiamiITS(), quotationDto.getTotal());

            JasperReport reportTemplate = getReportTemplateFor(quotation);
            int totalPages = jasperService.getTotalPages(reportTemplate, new IpQuotationReportDTO(quotationDto));
            pdfPath = jasperService.generatePDF(
                    reportTemplate,
                    new IpQuotationReportDTO(quotationDto),
                    quotation.getNumber(),
                    quotation.getCreatedAt(),
                    CONSECUTIVE_DEPARTMENT,
                    CONSECUTIVE_TYPE,
                    totalPages
            );
            quotation.setPdfUrl(pdfPath);
            quotationRepository.save(quotation);
            return jasperService.getPdfBytes(pdfPath);

        } catch (JRException | IOException ex) {
            throw new NotGenerateReportException(ex);
        }
    }

    private IpQuotationDTO toDto(IpQuotationEntity entity) {
        var dto = quotationMapper.entityToDTO(entity);
        loadClonedByQuotation(entity, dto);
        loadClonedQuotations(entity, dto);
        loadPurchaseOrders(entity, dto);
        return dto;
    }

    private void loadPurchaseOrders(IpQuotationEntity entity, IpQuotationDTO dto) {
        dto.setListPurchaseOrders(purchaseOrderRepository.fetchSummaryByQuotationId(entity.getId()));
    }

    private void loadClonedByQuotation(IpQuotationEntity entity, IpQuotationDTO dto) {
        clonedRepository.fetchByClonedId(entity.getId())
                .ifPresent(cloned -> dto.setClonedByQuotation(
                        quotationMapper.entityToDTO(cloned.getMainQuotation())
                ));
    }

    private void loadClonedQuotations(IpQuotationEntity entity, IpQuotationDTO dto) {
        var clonedQuotations = entity.getClonedQuotations();
        if (clonedQuotations != null && !clonedQuotations.isEmpty()) {
            dto.setClonedQuotations(
                clonedQuotations.stream()
                    .map(quotationMapper::entityToDTO)
                    .toList()
            );
        }
    }

    private boolean isOpenStatus(IpQuotationStatus status) {
        return status == IpQuotationStatus.CREATED
                || status == IpQuotationStatus.SENT
                || status == IpQuotationStatus.ANSWERED;
    }

    private boolean isFinalStatus(IpQuotationStatus status) {
        return status == IpQuotationStatus.COMPLETE
                || status == IpQuotationStatus.REJECTED;
    }

    private void clearOpenLockOnFinalStatus(IpQuotationEntity quotation) {
        if (isFinalStatus(quotation.getStatus())) {
            quotation.setOpenBy(null);
            quotation.setOpenAt(null);
        }
    }

    private JasperReport getReportTemplateFor(IpQuotationEntity quotation) {
        return Language.ENGLISH.equals(quotation.getClient().getLanguage())
                ? JasperReport.IP_Q_EN
                : JasperReport.IP_Q_ES;
    }

    private void validateOpenQuotation(IpQuotationEntity entity, com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity userAuthenticated) {
        if (entity.getOpenBy() == null)
            throw new NotExistIpQuotationException(simpleMessage("ip.q.not-block"));
        if (!entity.getOpenBy().getId().equals(userAuthenticated.getId()))
            throw new NotExistIpQuotationException(compositeMessage("ip.q.not-block-by", new String[]{entity.getOpenBy().getFullName()}));
    }
}
