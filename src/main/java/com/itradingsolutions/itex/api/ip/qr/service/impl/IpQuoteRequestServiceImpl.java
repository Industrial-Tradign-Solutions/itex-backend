package com.itradingsolutions.itex.api.ip.qr.service.impl;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveDepartment;
import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveModule;
import com.itradingsolutions.itex.api.common.consecutive.services.IConsecutiveService;
import com.itradingsolutions.itex.api.common.jasper.exceptions.NotGenerateReportException;
import com.itradingsolutions.itex.api.common.jasper.model.enums.JasperReport;
import com.itradingsolutions.itex.api.common.jasper.service.JasperService;
import com.itradingsolutions.itex.api.common.models.enums.OpenAndLockType;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.common.util.models.enums.Language;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationEntity;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationsQuoteRequestEntity;
import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationStatus;
import com.itradingsolutions.itex.api.ip.q.repository.IIpQuotationsQuoteRequestRepository;
import com.itradingsolutions.itex.api.ip.products.models.enums.IpProductStatus;
import com.itradingsolutions.itex.api.ip.qr.exceptions.NotChangeStatusException;
import com.itradingsolutions.itex.api.ip.qr.exceptions.NotExistIpQuoteRequestException;
import com.itradingsolutions.itex.api.ip.qr.exceptions.NotOpenIpQuoteRequestException;
import com.itradingsolutions.itex.api.ip.qr.exceptions.NotOpenQuoteRequestException;
import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestDTO;
import com.itradingsolutions.itex.api.ip.qr.models.dto.QuoteRequestProductStatusProjection;
import com.itradingsolutions.itex.api.ip.qr.models.dto.reports.IpQuoteRequestReportDTO;
import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestEntity;
import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestOtherChargesEntity;
import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestProductEntity;
import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestsClonedEntity;
import com.itradingsolutions.itex.api.ip.qr.models.enums.IpQuoteRequestStatus;
import com.itradingsolutions.itex.api.ip.qr.models.enums.IpQuoteRequestHistoryAction;
import com.itradingsolutions.itex.api.ip.qr.models.filters.FilterListIpQuoteRequest;
import com.itradingsolutions.itex.api.ip.qr.models.mappers.IpQuoteRequestMapper;
import com.itradingsolutions.itex.api.ip.qr.models.mappers.IpQuoteRequestOtherChargeMapper;
import com.itradingsolutions.itex.api.ip.qr.models.mappers.IpQuoteRequestProductMapper;
import com.itradingsolutions.itex.api.ip.qr.models.responses.ListIpQuoteRequestResponse;
import com.itradingsolutions.itex.api.ip.qr.repositories.IIpQuoteRequestClonedRepository;
import com.itradingsolutions.itex.api.ip.qr.repositories.IIpQuoteRequestOtherChargesRepository;
import com.itradingsolutions.itex.api.ip.qr.repositories.IIpQuoteRequestProductRepository;
import com.itradingsolutions.itex.api.ip.qr.repositories.IIpQuoteRequestRepository;
import com.itradingsolutions.itex.api.ip.qr.service.IIpQuoteRequestHistoryService;
import com.itradingsolutions.itex.api.ip.qr.service.IIpQuoteRequestService;
import com.itradingsolutions.itex.api.partners.clients.services.IClientContactService;
import com.itradingsolutions.itex.api.partners.clients.services.IClientService;
import com.itradingsolutions.itex.api.partners.suppliers.services.ISupplierContactService;
import com.itradingsolutions.itex.api.partners.suppliers.services.ISupplierService;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IpQuoteRequestServiceImpl extends UtilServiceAbs implements IIpQuoteRequestService {

    private final IUserService userService;
    private final IConsecutiveService consecutiveService;
    private final IIpQuoteRequestRepository qrRepository;
    private final IpQuoteRequestMapper qrMapper;

    private final IClientService clientService;
    private final IClientContactService clientContactService;
    private final ISupplierService supplierService;
    private final ISupplierContactService supplierContactService;

    private final IIpQuoteRequestProductRepository productRepository;
    private final IpQuoteRequestProductMapper productMapper;
    private final IIpQuotationsQuoteRequestRepository qqrRepository;
    private final IIpQuoteRequestClonedRepository clonedRepository;

    private final IpQuoteRequestOtherChargeMapper qrOtherChargesMapper;
    private final IIpQuoteRequestOtherChargesRepository qrOtherChargesRepository;

    private final IIpQuoteRequestHistoryService historyService;

    private static final ConsecutiveDepartment CONSECUTIVE_DEPARTMENT = ConsecutiveDepartment.IP;
    private static final ConsecutiveModule CONSECUTIVE_TYPE = ConsecutiveModule.QR;
    private static final int AUTO_REJECT_DAYS = 30;
    private static final ZoneId QR_ZONE_ID = ZoneId.of("America/New_York");

    private final JasperService jasperService;

    @Override
    @Transactional
    public IpQuoteRequestDTO createIpQuoteRequest(IpQuoteRequestDTO dto) {
        var entity = new IpQuoteRequestEntity();
        saveBaseInfo(entity);
        var resp = saveQuoteRequestInfo(dto, entity);
        consecutiveService.saveConsecutive(CONSECUTIVE_TYPE, CONSECUTIVE_DEPARTMENT, resp.getNumber());
        return resp;
    }

    @Override
    @Transactional(readOnly = true)
    public IpQuoteRequestDTO findIpQuoteRequestById(UUID id) {
        var qr = findById(id);
        return qrMapper.entityToDTO(qr);
    }

    @Override
    @Transactional
    public IpQuoteRequestDTO updateIpQuoteRequestById(UUID id, IpQuoteRequestDTO dto) {
        var entity = findById(id);
        var oldConsecutive = entity.getNumber();
        validateOpenQR(entity, userService.getUserAuthenticated());
        validateEditableStatus(entity);

        if (!entity.getSalesRep().getId().equals(dto.getSalesRep().getId())) {
            entity.setSalesRep(userService.findEntityById(dto.getSalesRep().getId(), true));
        }

        var resp = saveQuoteRequestInfo(dto, entity);

        if (!oldConsecutive.equalsIgnoreCase(resp.getNumber())) {
            consecutiveService.saveConsecutive(CONSECUTIVE_TYPE, CONSECUTIVE_DEPARTMENT, resp.getNumber());
            consecutiveService.deleteConsecutive(CONSECUTIVE_TYPE, CONSECUTIVE_DEPARTMENT, oldConsecutive);
        }

        clonedRepository.fetchByClonedId(id)
                .ifPresent(cloned -> resp.setClonedByQr(qrMapper.entityToDTO(cloned.getMainQr())));
        return resp;
    }

    @Override
    @Transactional
    public IpQuoteRequestDTO cloneIpQuoteRequest(UUID id) {
        var original = findById(id);
        var user = userService.getUserAuthenticated();
        if (isOpenStatus(original.getStatus()))
            validateOpenQR(original, user);
        validateMaxOpenQr(user.getId());
        var clone = qrMapper.clone(original);
        saveBaseInfo(clone);
        clone.setNumber(consecutiveService.generateConsecutive(CONSECUTIVE_TYPE, CONSECUTIVE_DEPARTMENT, clone.getClient().getCode()));
        clone = qrRepository.saveAndFlush(clone);
        consecutiveService.saveConsecutive(CONSECUTIVE_TYPE, CONSECUTIVE_DEPARTMENT, clone.getNumber());
        for (IpQuoteRequestProductEntity item : original.getProducts()) {
            var cloneItem = productMapper.clone(item);
            cloneItem.setIpQuoteRequest(clone);
            cloneItem.setCreatedAt(ZonedDateTime.now(QR_ZONE_ID));
            productRepository.save(cloneItem);
        }
        for (IpQuoteRequestOtherChargesEntity item : original.getOtherCharges()) {
            var cloneItem = qrOtherChargesMapper.clone(item);
            cloneItem.setIpQuoteRequest(clone);
            cloneItem.setCreatedAt(ZonedDateTime.now(QR_ZONE_ID));
            qrOtherChargesRepository.save(cloneItem);
        }
        var clonedItem = new IpQuoteRequestsClonedEntity();
        clonedItem.setId(original.getId(), clone.getId());
        clonedItem.setClonedQr(clone);
        clonedItem.setMainQr(original);
        clonedRepository.save(clonedItem);
        return qrMapper.entityToDTO(clone);
    }

    @Override
    @Transactional
    public IpQuoteRequestDTO openAndLockIpQuoteRequest(UUID id, OpenAndLockType type) {
        var quoteRequest = findById(id);
        if (quoteRequest.getOpenBy() == null)  {
            var user = userService.getUserAuthenticated();
            validateMaxOpenQr(user.getId());

            if (type.equals(OpenAndLockType.EDIT)) {
                quoteRequest.setOpenBy(user);
                quoteRequest.setOpenAt(ZonedDateTime.now(zoneId));
                quoteRequest = qrRepository.save(quoteRequest);
            }
        }
        var resp = qrMapper.entityToDTO(quoteRequest);
        clonedRepository.fetchByClonedId(id)
                .ifPresent(cloned -> resp.setClonedByQr(qrMapper.entityToDTO(cloned.getMainQr())));
        return resp;
    }

    private void validateMaxOpenQr(UUID idUser) {
        if (qrRepository.countByOpenUserId(idUser) >= maxTabsOpen)
            throw new NotOpenQuoteRequestException(compositeMessage("ip.qr.not-open-max", new String[]{maxTabsOpen.toString()}));
    }

    @Override
    @Transactional
    public void unlockIpQuoteRequest(UUID idQuoteRequest) {
        var quoteRequest = findById(idQuoteRequest);
        quoteRequest.setOpenBy(null);
        quoteRequest.setOpenAt(null);
        qrRepository.save(quoteRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IpQuoteRequestDTO> listAllOpenIpQuoteRequest(String username) {
        List<IpQuoteRequestEntity> list = qrRepository.fetchAllOpenByUsername(username);
        return list.stream().map(qrMapper::entityToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<IpQuoteRequestDTO> listAllOpenIpQuoteRequests() {
        List<IpQuoteRequestEntity> list = qrRepository.fetchAllOpen();
        return list.stream().map(qrMapper::entityToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ListIpQuoteRequestResponse> listAllQuoteRequests(Pageable pageable, FilterListIpQuoteRequest filters) {
        Specification<IpQuoteRequestEntity> spec = (filters == null ? Specification.where(null) : filters.filter());
        Page<IpQuoteRequestEntity> resp = qrRepository.findAll(spec, pageable);
        return new PageImpl<>(resp.getContent().stream().map(qrMapper::entityToListResponse).toList(), resp.getPageable(), resp.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<IpQuoteRequestDTO> listAllQuoteRequestsByStatus(IpQuoteRequestStatus status) {
        List<IpQuoteRequestEntity> listQR = qrRepository.fetchAllByStatus(status);
        return listQR.stream().map(qrMapper::entityToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public IpQuoteRequestEntity getEntityById(UUID id) {
        return findById(id);
    }

    private void saveBaseInfo(IpQuoteRequestEntity entity) {
        entity.setStatus(IpQuoteRequestStatus.CREATED);
        entity.setCreatedAt(ZonedDateTime.now(zoneId));
        entity.setOpenAt(ZonedDateTime.now(zoneId));
        var user = userService.getUserAuthenticated();
        entity.setOpenBy(user);
        entity.setSalesRep(user);
    }

    private IpQuoteRequestDTO saveQuoteRequestInfo(IpQuoteRequestDTO dto, IpQuoteRequestEntity entity) {
        entity.setCurrency(dto.getCurrency());
        entity.setClientQrNumber(dto.getClientQrNumber());
        entity.setSupplierQrNumber(dto.getSupplierQrNumber());
        entity.setRemarks(dto.getRemarks());
        entity.setInternalRemarks(dto.getInternalRemarks());
        entity.setShippingPointZipCode(dto.getShippingPointZipCode());
        entity.setFreightClass(dto.getFreightClass());
        entity.setFobShippingPoint(dto.getFobShippingPoint());
        entity.setFreightCharges(dto.getFreightCharges());

        if (entity.getClient() == null || !entity.getClient().getId().equals(dto.getClient().getId())) {
            entity.setClient(clientService.findClientById(dto.getClient().getId(), true));
            entity.setNumber(consecutiveService.generateConsecutive(CONSECUTIVE_TYPE, CONSECUTIVE_DEPARTMENT, entity.getClient().getCode()));
        }

        if (dto.getClient() != null && dto.getClientContact() != null) {
            if (entity.getClientContact() == null || !entity.getClientContact().getId().equals(dto.getClientContact().getId()))
                entity.setClientContact(clientContactService.findById(dto.getClientContact().getId(), dto.getClient().getId()));
        } else {
            entity.setClientContact(null);
        }

        if (entity.getSupplier() == null || !entity.getSupplier().getId().equals(dto.getSupplier().getId()))
            entity.setSupplier(supplierService.findSupplierById(dto.getSupplier().getId(), true));


        if (dto.getSupplier() != null && dto.getSupplierContact() != null) {
            if (entity.getSupplierContact() == null || !entity.getSupplierContact().getId().equals(dto.getSupplierContact().getId()))
                entity.setSupplierContact(supplierContactService.findById(dto.getSupplierContact().getId(), dto.getSupplier().getId()));
        } else {
            entity.setSupplierContact(null);
        }

        var userAuthenticated = userService.getUserAuthenticated();

        entity.setPaymentTerms(entity.getSupplier() != null ? entity.getSupplier().getPaymentTerms() : null);
        if (validateAction(userAuthenticated, ModuleAction.EDIT_PAYMENT_TERMS_IP_QUOTE_REQUESTS) && dto.getPaymentTerms() != null)
            entity.setPaymentTerms(dto.getPaymentTerms());

        return qrMapper.entityToDTO(qrRepository.save(entity));
    }

    private IpQuoteRequestEntity findById(UUID id) {
        return qrRepository.findById(id).orElseThrow(() -> new NotExistIpQuoteRequestException(simpleMessage("ip.qr.not-exist")));
    }

    @Override
    public void validateOpenQR(IpQuoteRequestEntity entity, UserEntity userAuthenticated) {
        if (entity.getOpenBy() == null)
            throw new NotOpenIpQuoteRequestException(simpleMessage("ip.qr.not-block"));
        if ( !entity.getOpenBy().getId().equals(userAuthenticated.getId()))
            throw new NotOpenIpQuoteRequestException(compositeMessage("ip.qr.not-block-by", new String[]{entity.getOpenBy().getFullName()}));
    }

    @Override
    @Transactional
    public IpQuoteRequestDTO rejectQuoteRequest(UUID qrId) {
        return changeStatus(qrId, IpQuoteRequestStatus.REJECTED);
    }

    @Override
    @Transactional
    public byte[] printQuoteRequest(UUID qrId) {
        IpQuoteRequestEntity quoteRequest = findById(qrId);
        IpQuoteRequestStatus status = quoteRequest.getStatus();

        if (isOpenStatus(status))
            validateOpenQR(quoteRequest, userService.getUserAuthenticated());

        if (quoteRequest.getProducts() == null || quoteRequest.getProducts().isEmpty())
            throw new NotGenerateReportException(simpleMessage("ip.qr.not-generate-doc"));
        try {
            String pdfPath = quoteRequest.getPdfUrl();
            if (isFinalStatus(status) && pdfPath != null)
                return jasperService.getPdfBytes(pdfPath);
            JasperReport reportTemplate = getReportTemplateFor(quoteRequest);

            int totalPages = jasperService.getTotalPages(reportTemplate, new IpQuoteRequestReportDTO(qrMapper.entityToDTO(quoteRequest)));

            pdfPath = jasperService.generatePDF(
                    reportTemplate,
                    new IpQuoteRequestReportDTO(qrMapper.entityToDTO(quoteRequest)),
                    quoteRequest.getNumber(),
                    quoteRequest.getCreatedAt(),
                    CONSECUTIVE_DEPARTMENT,
                    CONSECUTIVE_TYPE,
                    totalPages
            );
            quoteRequest.setPdfUrl(pdfPath);
            qrRepository.save(quoteRequest);
            return jasperService.getPdfBytes(pdfPath);

        } catch (JRException | IOException ex) {
            throw new NotGenerateReportException(ex);
        }
    }

    @Override
    @Transactional
    public IpQuoteRequestDTO changeStatusQuoteRequest(UUID qrId, IpQuoteRequestStatus newStatus) {
        return changeStatus(qrId, newStatus);
    }

    @Override
    @Transactional
    public void changeStatusInternal(UUID qrId, IpQuoteRequestStatus newStatus) {
        var qr = findById(qrId);
        var currentStatus = qr.getStatus();

        // Do not touch QRs already in the target status or in a terminal status
        if (currentStatus == newStatus || isFinalStatus(currentStatus)) {
            return;
        }

        setStatusTimestamp(qr, newStatus);
        if (newStatus.ordinal() < currentStatus.ordinal()) {
            clearFutureTimestamps(qr, newStatus);
        }
        qr.setStatus(newStatus);
        clearOpenLockOnFinalStatus(qr);
        qrRepository.save(qr);
    }

    @Override
    @Transactional
    public int autoRejectStaleQuoteRequests() {
        // Date-only comparison: a QR exceeded 30 days when its reference date is on or before
        // today - 30, which is equivalent to its timestamp being strictly before midnight of today - 29.
        var cutoff = LocalDate.now(zoneId).minusDays(AUTO_REJECT_DAYS - 1L).atStartOfDay(zoneId);

        return rejectStale(qrRepository.fetchStaleCreated(IpQuoteRequestStatus.CREATED, cutoff))
             + rejectStale(qrRepository.fetchStaleSent(IpQuoteRequestStatus.SENT, cutoff))
             + rejectStale(qrRepository.fetchStaleAnsweredWithoutQuotation(IpQuoteRequestStatus.ANSWERED, cutoff));
    }

    private int rejectStale(List<IpQuoteRequestEntity> staleQuoteRequests) {
        staleQuoteRequests.forEach(this::rejectStaleExpired);
        return staleQuoteRequests.size();
    }

    private void rejectStaleExpired(IpQuoteRequestEntity qr) {
        var oldStatus = qr.getStatus();
        changeStatusInternal(qr.getId(), IpQuoteRequestStatus.REJECTED);
        historyService.addHistoryAutoStatusChange(
                IpQuoteRequestHistoryAction.AUTO_REJECTED_TIME,
                qr.getId(),
                oldStatus,
                IpQuoteRequestStatus.REJECTED,
                null,
                qr.getSalesRep()
        );
    }

    @Override
    public void validateEditableStatus(IpQuoteRequestEntity entity) {
        if (isFinalStatus(entity.getStatus())) {
            throw new NotChangeStatusException(simpleMessage("ip.qr.not-editable-by-status"));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<IpQuoteRequestDTO> getListQuoteRequestByClientAvailableToQuotation(UUID clientId, boolean viewCompletedQR, Currency currency) {
        List<IpQuoteRequestStatus> status = new java.util.ArrayList<>(List.of(IpQuoteRequestStatus.ANSWERED));
        if (viewCompletedQR) status.add(IpQuoteRequestStatus.COMPLETE);
        var resp = qrRepository.fetchAllByClientAndStatus(clientId, status, currency);
        return resp.stream().map(qrMapper::entityToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public IpQuoteRequestEntity findByIdAndClient(UUID id, UUID clientId) {
        return qrRepository.fetchAllByIdAndClient(id, clientId).orElseThrow(() ->
            new NotExistIpQuoteRequestException(simpleMessage("ip.qr.not-exist"))
        );
    }

    private IpQuoteRequestDTO changeStatus(UUID qrId, IpQuoteRequestStatus newStatus) {
        var qr = findById(qrId);
        var currentStatus = qr.getStatus();

        validateNotSameStatus(qr, newStatus);
        if (newStatus == IpQuoteRequestStatus.COMPLETE) {
            validateManualComplete(currentStatus, userService.getUserAuthenticated());
        }
        validateSupplierRequiredForStatusChange(currentStatus, newStatus, qr);
        validateStatusRequirements(qr, newStatus);
        validateTerminalStatus(currentStatus);
        validateQuotationDependency(qr, currentStatus, newStatus);
        validateQuotationForReject(qr, newStatus);

        setStatusTimestamp(qr, newStatus);
        if (newStatus.ordinal() < currentStatus.ordinal()) {
            clearFutureTimestamps(qr, newStatus);
        }
        qr.setStatus(newStatus);
        clearOpenLockOnFinalStatus(qr);

        return qrMapper.entityToDTO(qrRepository.save(qr));
    }

    /**
     * A Quote Request is normally completed automatically when a Quotation containing
     * its products is answered. Manual completion is only allowed from an ANSWERED Quote
     * Request and requires the COMPLETE_IP_QUOTE_REQUESTS permission: a controlled fallback
     * for when the automatic flow cannot complete it for some reason.
     */
    private void validateManualComplete(IpQuoteRequestStatus currentStatus, UserEntity user) {
        if (!validateAction(user, ModuleAction.COMPLETE_IP_QUOTE_REQUESTS)) {
            throw new NotChangeStatusException(simpleMessage("ip.qr.no-manual-complete"));
        }
        if (currentStatus != IpQuoteRequestStatus.ANSWERED) {
            throw new NotChangeStatusException(simpleMessage("ip.qr.manual-complete-requires-answered"));
        }
    }

    private void validateSupplierRequiredForStatusChange(IpQuoteRequestStatus currentStatus, IpQuoteRequestStatus newStatus, IpQuoteRequestEntity qr) {
        if (requiresSupplierForStatusChange(currentStatus, newStatus) && qr.getSupplier() == null) {
            throw new NotChangeStatusException(
                compositeMessage("ip.qr.supplier.required.for.status.change", new String[]{newStatus.name()})
            );
        }
    }

    private boolean requiresSupplierForStatusChange(IpQuoteRequestStatus currentStatus, IpQuoteRequestStatus newStatus) {
        return switch (newStatus) {
            case SENT -> currentStatus == IpQuoteRequestStatus.CREATED;
            case ANSWERED -> currentStatus == IpQuoteRequestStatus.SENT;
            default -> false;
        };
    }

    private void validateNotSameStatus(IpQuoteRequestEntity qr, IpQuoteRequestStatus newStatus) {
        if (newStatus.equals(qr.getStatus()))
            throw new NotChangeStatusException(simpleMessage("ip.qr.equal-status"));
    }

    private void validateStatusRequirements(IpQuoteRequestEntity qr, IpQuoteRequestStatus newStatus) {
        if (newStatus == IpQuoteRequestStatus.ANSWERED && (!qr.isValidAnswered() || qr.getSentAt() == null))
            throw new NotChangeStatusException(simpleMessage("ip.qr.not-valid-answered"));
        if (newStatus == IpQuoteRequestStatus.ANSWERED) {
            var notActiveProducts = productRepository.fetchProductsNotInStatus(qr.getId(), IpProductStatus.ACTIVE);
            if (!notActiveProducts.isEmpty())
                throw new NotChangeStatusException(compositeMessage(
                        "ip.qr.products-not-active",
                        new String[]{formatNotActiveProducts(notActiveProducts)}
                ));
        }
    }

    private static String formatNotActiveProducts(List<QuoteRequestProductStatusProjection> notActiveProducts) {
        return notActiveProducts.stream()
                .map(projection -> {
                    var mfrReference = projection.mfrReference();
                    var description = projection.description();
                    var status = projection.status().name();
                    var label = StringUtils.hasText(mfrReference) ? mfrReference : description;
                    return label + " (" + status + ")";
                })
                .collect(Collectors.joining(", "));
    }

    private void validateTerminalStatus(IpQuoteRequestStatus currentStatus) {
        if (currentStatus == IpQuoteRequestStatus.COMPLETE)
            throw new NotChangeStatusException(simpleMessage("ip.qr.cannot-change-complete-status"));
        if (currentStatus == IpQuoteRequestStatus.REJECTED)
            throw new NotChangeStatusException(simpleMessage("ip.qr.cannot-change-rejected-status"));
    }

    private void validateQuotationDependency(IpQuoteRequestEntity qr, IpQuoteRequestStatus currentStatus, IpQuoteRequestStatus newStatus) {
        if (currentStatus == IpQuoteRequestStatus.ANSWERED &&
            (newStatus == IpQuoteRequestStatus.SENT || newStatus == IpQuoteRequestStatus.CREATED)) {
            validateQuotationChangeStatus(qr);
        }
    }

    private void validateQuotationChangeStatus(IpQuoteRequestEntity qr) {
        if (qqrRepository.existsByQuoteRequest_Id(qr.getId())) {
            throw new NotChangeStatusException(simpleMessage("ip.qr.assigned-to-q"));
        }
    }

    private void validateQuotationForReject(IpQuoteRequestEntity qr, IpQuoteRequestStatus newStatus) {
        if (newStatus != IpQuoteRequestStatus.REJECTED) return;

        if (qqrRepository.countByQuoteRequestIdAndQuotationStatusNot(qr.getId(), IpQuotationStatus.REJECTED) > 0) {
            throw new NotChangeStatusException(simpleMessage("ip.qr.assigned-to-q-rejected"));
        }
    }

    private void clearOpenLockOnFinalStatus(IpQuoteRequestEntity qr) {
        if (isFinalStatus(qr.getStatus())) {
            qr.setOpenBy(null);
            qr.setOpenAt(null);
        }
    }

    private void clearFutureTimestamps(IpQuoteRequestEntity qr, IpQuoteRequestStatus newStatus) {
        switch (newStatus) {
            case CREATED -> {
                qr.setSentAt(null);
                qr.setAnsweredAt(null);
            }
            case SENT ->
                qr.setAnsweredAt(null);

        }
    }

    private void setStatusTimestamp(IpQuoteRequestEntity qr, IpQuoteRequestStatus newStatus) {
        var now = ZonedDateTime.now(QR_ZONE_ID);
        switch (newStatus) {
            case ANSWERED -> qr.setAnsweredAt(now);
            case SENT -> qr.setSentAt(now);
            case COMPLETE -> qr.setCompleteAt(now);
            case REJECTED -> qr.setRejectAt(now);
            case CREATED -> { /* no timestamp */ }
        }
    }

    private boolean isOpenStatus(IpQuoteRequestStatus status) {
        return status == IpQuoteRequestStatus.CREATED
                || status == IpQuoteRequestStatus.SENT
                || status == IpQuoteRequestStatus.ANSWERED;
    }

    private boolean isFinalStatus(IpQuoteRequestStatus status) {
        return status == IpQuoteRequestStatus.COMPLETE
                || status == IpQuoteRequestStatus.REJECTED;
    }

    private JasperReport getReportTemplateFor(IpQuoteRequestEntity quoteRequest) {
        return Language.ENGLISH.equals(quoteRequest.getSupplier().getLanguage())
                ? JasperReport.IP_QR_EN
                : JasperReport.IP_QR_ES;
    }
}
