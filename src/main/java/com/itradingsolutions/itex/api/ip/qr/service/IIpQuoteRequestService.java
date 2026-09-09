package com.itradingsolutions.itex.api.ip.qr.service;

import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import com.itradingsolutions.itex.api.common.models.enums.OpenAndLockType;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestDTO;
import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestEntity;
import com.itradingsolutions.itex.api.ip.qr.models.enums.IpQuoteRequestStatus;
import com.itradingsolutions.itex.api.ip.qr.models.filters.FilterListIpQuoteRequest;
import com.itradingsolutions.itex.api.ip.qr.models.responses.ListIpQuoteRequestResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
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
    Page<ListIpQuoteRequestResponse> listAllQuoteRequests(Pageable pageable, FilterListIpQuoteRequest filters);
    List<IpQuoteRequestDTO> listAllQuoteRequestsByStatus(IpQuoteRequestStatus status);
    IpQuoteRequestEntity getEntityById(UUID id);
    void validateOpenQR(IpQuoteRequestEntity entity, UserEntity userAuthenticated);
    IpQuoteRequestDTO rejectQuoteRequest(UUID qrId);
    byte[] printQuoteRequest(UUID qrId);
    IpQuoteRequestDTO changeStatusQuoteRequest(UUID qrId, IpQuoteRequestStatus newStatus);

    /**
     * Changes the QR status without validating Quotation dependencies.
     * Intended for automatic, system-driven transitions (e.g. when a Quotation
     * is answered and its linked QRs must be completed or rejected in cascade).
     * QRs already in a terminal status (COMPLETE/REJECTED) or already in the
     * target status are left untouched.
     */
    void changeStatusInternal(UUID qrId, IpQuoteRequestStatus newStatus);

    /**
     * Automatically rejects stale Quote Requests by their age:
     * CREATED older than 30 days (by createdAt), SENT older than 30 days
     * (by sentAt) and ANSWERED older than 30 days with no Quotation linked
     * (by answeredAt). The age comparison is date-only (time is ignored),
     * evaluated in the America/New_York timezone. Each rejection is recorded
     * in the QR history as AUTO_REJECTED_TIME attributed to the QR's sales
     * representative. Returns the total number of rejected Quote Requests.
     */
    int autoRejectStaleQuoteRequests();

    /**
     * Validates that the QR is editable: it must not be in REJECTED or COMPLETE status.
     * Read-only operations, clone and print are not affected by this validation.
     */
    void validateEditableStatus(IpQuoteRequestEntity entity);

    /*
    Funcion para listar todos los QR validos para crear una cotizacion teniendo en cuenta que carga solo status Answered y si quieren los Completed
     */
    List<IpQuoteRequestDTO> getListQuoteRequestByClientAvailableToQuotation(UUID clientId, boolean viewCompletedQR, Currency currency);

    /*
    Funcion para buscar y validar que la quote request sea del mismo cliente
     */
    IpQuoteRequestEntity findByIdAndClient(UUID id, UUID clientId);
}
