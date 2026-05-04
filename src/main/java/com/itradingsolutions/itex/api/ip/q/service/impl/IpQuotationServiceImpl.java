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
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationDTO;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationEntity;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationsQuoteRequestEntity;
import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationStatus;
import com.itradingsolutions.itex.api.ip.q.models.filters.FilterListIpQuotation;
import com.itradingsolutions.itex.api.ip.q.models.mapper.IpQuotationMapper;
import com.itradingsolutions.itex.api.ip.q.models.requests.CreateIpQuotationRequest;
import com.itradingsolutions.itex.api.ip.q.models.requests.UpdateIpQuotationRequest;
import com.itradingsolutions.itex.api.ip.q.repository.IIpQuotationsQuoteRequestRepository;
import com.itradingsolutions.itex.api.ip.q.repository.IpQuotationRepository;
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
        return quotationMapper.entityToDTO(resp);
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
        if (request.paymentTerms() != null) quotation.setPaymentTerms(request.paymentTerms());

        return quotationMapper.entityToDTO(quotationRepository.save(quotation));
    }

    @Override
    @Transactional
    public IpQuotationDTO changeStatusQuotation(UUID id, IpQuotationStatus status) {
        var quotation = findById(id);
        quotation.setStatus(status);
        if (status == IpQuotationStatus.SENT) quotation.setSentAt(ZonedDateTime.now(zoneId));
        else if (status == IpQuotationStatus.ANSWERED) quotation.setAnsweredAt(ZonedDateTime.now(zoneId));
        else if (status == IpQuotationStatus.COMPLETE) quotation.setCompleteAt(ZonedDateTime.now(zoneId));
        return quotationMapper.entityToDTO(quotationRepository.save(quotation));
    }

    @Override
    @Transactional
    public IpQuotationDTO rejectQuotation(UUID id) {
        var quotation = findById(id);
        quotation.setStatus(IpQuotationStatus.REJECTED);
        quotation.setRejectAt(ZonedDateTime.now(zoneId));
        return quotationMapper.entityToDTO(quotationRepository.save(quotation));
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
}
