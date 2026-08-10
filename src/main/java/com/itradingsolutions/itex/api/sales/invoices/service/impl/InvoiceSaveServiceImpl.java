package com.itradingsolutions.itex.api.sales.invoices.service.impl;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveDepartment;
import com.itradingsolutions.itex.api.common.salesconsecutive.models.enums.SalesConsecutiveType;
import com.itradingsolutions.itex.api.common.salesconsecutive.services.ISalesConsecutiveService;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.masters.location.services.ICityService;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientContactEntity;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientEntity;
import com.itradingsolutions.itex.api.partners.clients.services.IClientContactService;
import com.itradingsolutions.itex.api.partners.clients.services.IClientService;
import com.itradingsolutions.itex.api.sales.invoices.exceptions.InvoiceAccessDeniedException;
import com.itradingsolutions.itex.api.sales.invoices.exceptions.InvoiceMaxOpenException;
import com.itradingsolutions.itex.api.sales.invoices.exceptions.NotExistInvoiceException;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceHistoryAction;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceStatus;
import com.itradingsolutions.itex.api.sales.invoices.models.mapper.InvoiceMapper;
import com.itradingsolutions.itex.api.sales.invoices.models.request.CreateInvoiceRequest;
import com.itradingsolutions.itex.api.sales.invoices.models.request.UpdateInvoiceRequest;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoiceRepository;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceHistoryService;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceSaveService;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceAccessGuard;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceClientValidator;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceDetailResolver;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceMutationGuard;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceShipToResolver;
import com.itradingsolutions.itex.api.sales.invoices.service.InvoiceShipToSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceSaveServiceImpl extends UtilServiceAbs implements IInvoiceSaveService {

    private static final ConsecutiveDepartment DEFAULT_DEPARTMENT = ConsecutiveDepartment.IP;
    private static final Currency DEFAULT_CURRENCY = Currency.USD;

    private final IInvoiceRepository repository;
    private final InvoiceMapper mapper;
    private final InvoiceAccessGuard accessGuard;
    private final InvoiceMutationGuard mutationGuard;
    private final InvoiceClientValidator clientValidator;
    private final InvoiceShipToResolver shipToResolver;
    private final InvoiceDetailResolver detailResolver;
    private final ISalesConsecutiveService salesConsecutiveService;
    private final IInvoiceHistoryService invoiceHistoryService;
    private final IUserService userService;
    private final IClientService clientService;
    private final IClientContactService clientContactService;
    private final ICityService cityService;

    @Override
    @Transactional
    public InvoiceDTO create(CreateInvoiceRequest request) {
        var user = userService.getUserAuthenticated();
        validateMaxOpen(user.getId());

        var department = request.department() != null ? request.department() : DEFAULT_DEPARTMENT;
        var client = clientService.findClientById(request.clientId(), true);
        var contact = resolveContact(client, request.clientContactId());

        clientValidator.validate(client, contact);

        var invoice = new InvoiceEntity();
        var now = ZonedDateTime.now(zoneId);

        invoice.setStatus(InvoiceStatus.DRAFT);
        invoice.setDepartment(department);
        invoice.setDraftNumber(salesConsecutiveService.generate(SalesConsecutiveType.DRAFT_INV));
        invoice.setCreatedAt(now);
        invoice.setOpenAt(now);
        invoice.setOpenBy(user);
        invoice.setSalesRep(user);
        invoice.setTotalAmount(BigDecimal.ZERO);
        invoice.setPaidAmount(BigDecimal.ZERO);
        invoice.setOverdue(false);

        invoice.setClient(client);
        invoice.setClientContact(contact);
        applyShipToFromClient(invoice, shipToResolver.resolve(client, contact));

        invoice.setCurrency(request.currency() != null ? request.currency() : DEFAULT_CURRENCY);
        invoice.setIncoterms(request.incoterms());
        invoice.setPaymentTerms(defaultPaymentTerms(client));
        invoice.setVia(request.via());

        applyFreeFields(invoice, mapper.createRequestToDTO(request));

        var dto = detailResolver.resolve(repository.save(invoice));
        invoiceHistoryService.addHistory(InvoiceHistoryAction.CREATE, null, dto);
        return dto;
    }

    @Override
    @Transactional
    public InvoiceDTO update(UUID id, UpdateInvoiceRequest request) {
        var invoice = findById(id);
        accessGuard.assertCanAccess(invoice);
        accessGuard.assertCanMutate(invoice);
        mutationGuard.assertEditable(invoice);
        mutationGuard.assertLockedByCurrentUser(invoice);

        // Snapshot before mutating: the mapper builds a fresh DTO, so it survives the changes below.
        var oldDto = mapper.entityToDTO(invoice);
        var user = userService.getUserAuthenticated();

        applyClientAndContact(invoice, request);
        applyShipToFromRequest(invoice, request);
        applyFreeFields(invoice, mapper.updateRequestToDTO(request));
        invoice.setCurrency(request.currency());
        invoice.setIncoterms(request.incoterms());
        invoice.setVia(request.via());
        applyPaymentTerms(invoice, request, user);
        applySalesRep(invoice, request, user);

        var newDto = detailResolver.resolve(repository.save(invoice));
        invoiceHistoryService.addHistory(InvoiceHistoryAction.UPDATE, oldDto, newDto);
        return newDto;
    }

    /**
     * Resolves the contact on every update, not just on a client change: an explicit
     * {@code clientContactId} always wins, otherwise the Client's main active contact (any
     * department) is used. The resulting Client + contact pair must be invoiceable, the same
     * question {@link #create} asks.
     */
    private void applyClientAndContact(InvoiceEntity invoice, UpdateInvoiceRequest request) {
        if (!invoice.getClient().getId().equals(request.clientId()))
            invoice.setClient(clientService.findClientById(request.clientId(), true));

        var contact = resolveContact(invoice.getClient(), request.clientContactId());
        clientValidator.validate(invoice.getClient(), contact);
        invoice.setClientContact(contact);
    }

    /**
     * On update the ship to block always comes from the request. Auto-filling only happens at
     * creation; if the user switches Client, the frontend asks for the new defaults.
     */
    private void applyShipToFromRequest(InvoiceEntity invoice, UpdateInvoiceRequest request) {
        invoice.setShipToName(request.shipToName());
        invoice.setShipToAddress(request.shipToAddress());
        invoice.setShipToPhone(request.shipToPhone());
        invoice.setShipToContactName(request.shipToContactName());
        invoice.setShipToEmail(request.shipToEmail());

        if (invoice.getShipToCity() == null || !invoice.getShipToCity().getId().equals(request.shipToCityId()))
            invoice.setShipToCity(cityService.findEntityById(request.shipToCityId()));
    }

    private void applyShipToFromClient(InvoiceEntity invoice, InvoiceShipToSnapshot shipTo) {
        invoice.setShipToName(shipTo.name());
        invoice.setShipToAddress(shipTo.address());
        invoice.setShipToCity(shipTo.city());
        invoice.setShipToPhone(shipTo.phone());
        invoice.setShipToContactName(shipTo.contactName());
        invoice.setShipToEmail(shipTo.email());
    }

    /**
     * Free text fields are read from the DTO rather than the request: {@code InvoiceDTO} trims them
     * on the way in.
     */
    private void applyFreeFields(InvoiceEntity invoice, InvoiceDTO normalized) {
        invoice.setOrderNumber(normalized.getOrderNumber());
        invoice.setAwbBl(normalized.getAwbBl());
        invoice.setRemarks(normalized.getRemarks());
        invoice.setInternalRemarks(normalized.getInternalRemarks());
        invoice.setPackingList(normalized.getPackingList());
    }

    /**
     * The Client's terms win unless the caller holds {@code EDIT_PAYMENT_TERMS_INVOICE}.
     */
    private void applyPaymentTerms(InvoiceEntity invoice, UpdateInvoiceRequest request, UserEntity user) {
        invoice.setPaymentTerms(defaultPaymentTerms(invoice.getClient()));

        if (request.paymentTerms() != null && validateAction(user, ModuleAction.EDIT_PAYMENT_TERMS_INVOICE))
            invoice.setPaymentTerms(request.paymentTerms());
    }

    /**
     * Only reacts to an actual reassignment — the frontend echoes back the current sales rep, and
     * that must not fail for users without the permission.
     */
    private void applySalesRep(InvoiceEntity invoice, UpdateInvoiceRequest request, UserEntity user) {
        if (request.salesRepId() == null)
            return;

        var currentId = invoice.getSalesRep() != null ? invoice.getSalesRep().getId() : null;
        if (request.salesRepId().equals(currentId))
            return;

        if (!validateAction(user, ModuleAction.CHANGE_SALES_REP_INVOICE))
            throw new InvoiceAccessDeniedException(simpleMessage("sales.invoice.sales-rep-not-allowed"));

        invoice.setSalesRep(userService.findEntityById(request.salesRepId(), true));
    }

    private ClientContactEntity resolveContact(ClientEntity client, UUID contactId) {
        return contactId == null
                ? shipToResolver.defaultContact(client)
                : clientContactService.findById(contactId, client.getId());
    }

    /**
     * A Client without payment terms falls back to {@code TO_BE_AGREED}, which leaves
     * {@code due_at} null and keeps the invoice out of the overdue calculation.
     */
    private static PaymentTerms defaultPaymentTerms(ClientEntity client) {
        return client.getPaymentTerms() != null ? client.getPaymentTerms() : PaymentTerms.TO_BE_AGREED;
    }

    private void validateMaxOpen(UUID userId) {
        if (repository.countByOpenUserId(userId) >= maxTabsOpen)
            throw new InvoiceMaxOpenException(compositeMessage("sales.invoice.not-open-max", new String[]{maxTabsOpen.toString()}));
    }

    private InvoiceEntity findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NotExistInvoiceException(simpleMessage("sales.invoice.not-exist")));
    }
}
