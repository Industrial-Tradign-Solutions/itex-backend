package com.itradingsolutions.itex.api.ip.q.service.impl;

import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveDepartment;
import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveModule;
import com.itradingsolutions.itex.api.common.consecutive.services.IConsecutiveService;
import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.common.models.enums.OpenAndLockType;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.ip.q.exceptions.NotExistIpQuotationException;
import com.itradingsolutions.itex.api.ip.q.exceptions.QuotationCurrencyMismatchException;
import com.itradingsolutions.itex.api.ip.q.exceptions.QuoteRequestAlreadyLinkedException;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationDTO;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationEntity;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationProductEntity;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationsQuoteRequestEntity;
import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationHistoryAction;
import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationStatus;
import com.itradingsolutions.itex.api.ip.q.models.filters.FilterListIpQuotation;
import com.itradingsolutions.itex.api.ip.q.models.mapper.IpQuotationMapper;
import com.itradingsolutions.itex.api.ip.q.models.requests.CreateIpQuotationRequest;
import com.itradingsolutions.itex.api.ip.q.models.requests.UpdateIpQuotationRequest;
import com.itradingsolutions.itex.api.ip.q.repository.IIpQuotationsQuoteRequestRepository;
import com.itradingsolutions.itex.api.ip.q.repository.IpQuotationRepository;
import com.itradingsolutions.itex.api.ip.q.service.IIpQuotationHistoryService;
import com.itradingsolutions.itex.api.ip.q.service.IpQuotationService;
import com.itradingsolutions.itex.api.ip.qr.exceptions.NotOpenQuoteRequestException;
import com.itradingsolutions.itex.api.ip.qr.service.IIpQuoteRequestService;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientEntity;
import com.itradingsolutions.itex.api.partners.clients.repository.IClientContactRepository;
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
import java.util.UUID;

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
    private final IIpQuotationHistoryService historyService;

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
        return quotationMapper.entityToDTO(quotation);
    }

    @Override
    @Transactional
    public IpQuotationDTO updateQuotation(UUID id, UpdateIpQuotationRequest request) {
        var quotation = findById(id);
        validateEditable(quotation);
        
        var oldDto = quotationMapper.entityToDTO(quotation);

        if (request.salesRepId() != null)
            quotation.setSalesRep(userService.findEntityById(request.salesRepId(), true));

        if (request.clientContactId() != null) {
            quotation.setClientContact(
                    clientContactRepository.findById(request.clientContactId()).orElse(null)
            );
        } else {
            quotation.setClientContact(null);
        }

        quotation.setClientQNumber(request.clientQrNumber());
        quotation.setRemarks(request.remarks());
        quotation.setInternalRemarks(request.internalRemarks());

        if (request.leadTime() != null) quotation.setLeadTime(request.leadTime());
        if (request.leadTimeType() != null) quotation.setLeadTimeType(request.leadTimeType());
        if (request.validity() != null) quotation.setValidity(request.validity());
        if (request.validityType() != null) quotation.setValidityType(request.validityType());
        if (request.incoterms() != null) quotation.setIncoterms(request.incoterms());
        
        // Payment Terms requires special permission EDIT_PAYMENT_TERMS_IP_QUOTATIONS (4003006)
        // The @AccessToAction annotation on the controller method UPDATE_IP_QUOTATIONS
        // already validates access, but editing payment terms is an additional privilege
        // that can be controlled separately through role permissions
        if (request.paymentTerms() != null) quotation.setPaymentTerms(request.paymentTerms());

        var saved = quotationRepository.save(quotation);
        var newDto = quotationMapper.entityToDTO(saved);
        
        // Register UPDATE history
        historyService.addHistory(IpQuotationHistoryAction.UPDATE, oldDto, newDto);
        
        return newDto;
    }

    @Override
    @Transactional
    public IpQuotationDTO changeStatusQuotation(UUID id, IpQuotationStatus status) {
        var quotation = findById(id);
        var oldDto = quotationMapper.entityToDTO(quotation);
        
        quotation.setStatus(status);
        if (status == IpQuotationStatus.SENT) quotation.setSentAt(ZonedDateTime.now(zoneId));
        else if (status == IpQuotationStatus.ANSWERED) quotation.setAnsweredAt(ZonedDateTime.now(zoneId));
        else if (status == IpQuotationStatus.COMPLETE) quotation.setCompleteAt(ZonedDateTime.now(zoneId));
        
        var saved = quotationRepository.save(quotation);
        var newDto = quotationMapper.entityToDTO(saved);
        
        // Register STATUS_CHANGE history
        historyService.addHistory(IpQuotationHistoryAction.STATUS_CHANGE, oldDto, newDto);
        
        return newDto;
    }

    @Override
    @Transactional
    public IpQuotationDTO rejectQuotation(UUID id) {
        var quotation = findById(id);
        var oldDto = quotationMapper.entityToDTO(quotation);
        
        quotation.setStatus(IpQuotationStatus.REJECTED);
        quotation.setRejectAt(ZonedDateTime.now(zoneId));
        
        var saved = quotationRepository.save(quotation);
        var dto = quotationMapper.entityToDTO(saved);
        
        // Register REJECTED history
        historyService.addHistory(IpQuotationHistoryAction.REJECTED, oldDto, dto);
        
        return dto;
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
        
        var saved = quotationRepository.save(cloned);
        consecutiveService.saveConsecutive(CONSECUTIVE_TYPE, CONSECUTIVE_DEPARTMENT, saved.getNumber());
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
}
