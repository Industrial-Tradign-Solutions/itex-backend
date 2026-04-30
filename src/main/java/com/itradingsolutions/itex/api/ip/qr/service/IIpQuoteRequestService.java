package com.itradingsolutions.itex.api.ip.qr.service;

import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import com.itradingsolutions.itex.api.common.models.enums.OpenAndLockType;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestDTO;
import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestEntity;
import com.itradingsolutions.itex.api.ip.qr.models.enums.IpQuoteRequestStatus;
import com.itradingsolutions.itex.api.ip.qr.models.filters.FilterListIpQuoteRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IIpQuoteRequestService {
    IpQuoteRequestDTO createIpQuoteRequest(IpQuoteRequestDTO quoteRequest);
    IpQuoteRequestDTO findIpQuoteRequestById(UUID id);
    IpQuoteRequestDTO updateIpQuoteRequestById(UUID id, IpQuoteRequestDTO request);
    IpQuoteRequestDTO openAndLockIpQuoteRequest(UUID id, OpenAndLockType type);
    IpQuoteRequestDTO cloneIpQuoteRequest(UUID id);
    void unlockIpQuoteRequest(UUID idQuoteRequest);
    List<IpQuoteRequestDTO> listAllOpenIpQuoteRequest(String username);
    List<IpQuoteRequestDTO> listAllOpenIpQuoteRequests();
    Page<IpQuoteRequestDTO> listAllQuoteRequests(Pageable pageable, FilterListIpQuoteRequest filters);
    List<IpQuoteRequestDTO> listAllQuoteRequestsByStatus(IpQuoteRequestStatus status);
    IpQuoteRequestEntity getEntityById(UUID id);
    void validateOpenQR(IpQuoteRequestEntity entity, UserEntity userAuthenticated);
    IpQuoteRequestDTO rejectQuoteRequest(UUID qrId);
    byte[] printQuoteRequest(UUID qrId);
    IpQuoteRequestDTO changeStatusQuoteRequest(UUID qrId, IpQuoteRequestStatus newStatus);

    /*
    Funcion para listar todos los QR validos para crear una cotizacion teniendo en cuenta que carga solo status Answered y si quieren los Completed
     */
    List<IpQuoteRequestDTO> getListQuoteRequestByClientAvailableToQuotation(UUID clientId, boolean viewCompletedQR, Currency currency);

    /*
    Funcion para buscar y validar que la quote request sea del mismo cliente
     */
    IpQuoteRequestEntity findByIdAndClient(UUID id, UUID clientId);
}
