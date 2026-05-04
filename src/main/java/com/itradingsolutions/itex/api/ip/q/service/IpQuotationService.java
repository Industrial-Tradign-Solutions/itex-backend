package com.itradingsolutions.itex.api.ip.q.service;

import com.itradingsolutions.itex.api.common.models.enums.OpenAndLockType;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationDTO;
import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationStatus;
import com.itradingsolutions.itex.api.ip.q.models.filters.FilterListIpQuotation;
import com.itradingsolutions.itex.api.ip.q.models.requests.CreateIpQuotationRequest;
import com.itradingsolutions.itex.api.ip.q.models.requests.UpdateIpQuotationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IpQuotationService {

    void unlockIpQuotation(UUID idQuotation);
    List<IpQuotationDTO> listAllOpenIpQuotation(String username);
    List<IpQuotationDTO> listAllOpenIpQuotation();
    Page<IpQuotationDTO> listAllQuotations(Pageable pageable, FilterListIpQuotation filters);
    IpQuotationDTO createQuotation(CreateIpQuotationRequest request);
    IpQuotationDTO openAndLockIpQuotation(UUID id, OpenAndLockType type);
    IpQuotationDTO updateQuotation(UUID id, UpdateIpQuotationRequest request);
    IpQuotationDTO changeStatusQuotation(UUID id, IpQuotationStatus status);
    IpQuotationDTO rejectQuotation(UUID id);
    void removeQuoteRequestFromQuotation(UUID quotationId, UUID qqrId);
    IpQuotationDTO addQuoteRequestsToQuotation(UUID quotationId, List<UUID> quoteRequestIds);
    IpQuotationDTO cloneQuotation(UUID id);
}
