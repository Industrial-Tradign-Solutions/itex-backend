package com.itradingsolutions.itex.api.ip.po.service.impl;

import com.itradingsolutions.itex.api.common.models.entities.BaseEntity;
import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveDepartment;
import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveModule;
import com.itradingsolutions.itex.api.common.consecutive.services.IConsecutiveService;
import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.common.models.enums.OpenAndLockType;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.ip.po.exceptions.InvalidSupplierForIpPurchaseOrderException;
import com.itradingsolutions.itex.api.ip.po.exceptions.IpPurchaseOrderAnsweredLockedException;
import com.itradingsolutions.itex.api.ip.po.exceptions.IpPurchaseOrderNotEditableException;
import com.itradingsolutions.itex.api.ip.po.exceptions.NotExistIpPurchaseOrderException;
import com.itradingsolutions.itex.api.ip.po.exceptions.NotOpenIpPoException;
import com.itradingsolutions.itex.api.ip.po.exceptions.NotOpenIpPurchaseOrderException;
import com.itradingsolutions.itex.api.ip.po.models.dto.IpPurchaseOrderDTO;
import com.itradingsolutions.itex.api.ip.po.models.entities.IpPurchaseOrderEntity;
import com.itradingsolutions.itex.api.ip.po.models.entities.IpPurchaseOrdersClonedEntity;
import com.itradingsolutions.itex.api.ip.po.models.enums.IpPurchaseOrderStatus;
import com.itradingsolutions.itex.api.ip.po.models.filters.FilterListIpPurchaseOrder;
import com.itradingsolutions.itex.api.ip.po.models.mapper.IpPurchaseOrderMapper;
import com.itradingsolutions.itex.api.ip.po.models.mapper.IpPurchaseOrderOtherChargeMapper;
import com.itradingsolutions.itex.api.ip.po.models.mapper.IpPurchaseOrderOtherChargesQuotationMapper;
import com.itradingsolutions.itex.api.ip.po.models.mapper.IpPurchaseOrderOtherChargesQuotationQrMapper;
import com.itradingsolutions.itex.api.ip.po.models.mapper.IpPurchaseOrderProductMapper;
import com.itradingsolutions.itex.api.ip.po.models.request.CreateIpPurchaseOrderRequest;
import com.itradingsolutions.itex.api.ip.po.models.request.UpdateIpPurchaseOrderRequest;
import com.itradingsolutions.itex.api.ip.po.repository.IIpPurchaseOrderRepository;
import com.itradingsolutions.itex.api.ip.po.repository.IIpPurchaseOrdersClonedRepository;
import com.itradingsolutions.itex.api.ip.po.service.IIpPurchaseOrderService;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationEntity;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationsQuoteRequestEntity;
import com.itradingsolutions.itex.api.ip.q.repository.IpQuotationRepository;
import com.itradingsolutions.itex.api.ip.qr.models.enums.IpQuoteRequestStatus;
import com.itradingsolutions.itex.api.ip.qr.service.IIpQuoteRequestService;
import com.itradingsolutions.itex.api.masters.location.models.entities.CityEntity;
import com.itradingsolutions.itex.api.masters.location.services.ICityService;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientEntity;
import com.itradingsolutions.itex.api.partners.clients.services.IClientContactService;
import com.itradingsolutions.itex.api.partners.clients.services.IClientService;
import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierEntity;
import com.itradingsolutions.itex.api.partners.suppliers.services.ISupplierContactService;
import com.itradingsolutions.itex.api.partners.suppliers.services.ISupplierService;
import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class IpPurchaseOrderServiceImpl extends UtilServiceAbs implements IIpPurchaseOrderService {

    private final IIpPurchaseOrderRepository repository;
    private final IpPurchaseOrderMapper mapper;
    private final IConsecutiveService consecutiveService;
    private final IUserService userService;
    private final IClientService clientService;
    private final IClientContactService clientContactService;
    private final ISupplierService supplierService;
    private final ISupplierContactService supplierContactService;
    private final ICityService cityService;
    private final IpQuotationRepository quotationRepository;
    private final IIpQuoteRequestService qrService;
    private final IIpPurchaseOrdersClonedRepository clonedRepository;
    private final IpPurchaseOrderProductMapper productMapper;
    private final IpPurchaseOrderOtherChargeMapper otherChargeMapper;
    private final IpPurchaseOrderOtherChargesQuotationMapper importedQChargeMapper;
    private final IpPurchaseOrderOtherChargesQuotationQrMapper importedQrChargeMapper;

    private static final ConsecutiveDepartment CONSECUTIVE_DEPARTMENT = ConsecutiveDepartment.IP;
    private static final ConsecutiveModule CONSECUTIVE_MODULE = ConsecutiveModule.PO;

    private static final String SHIP_TO_NAME = "INDUSTRIAL TRADING SOLUTIONS CORP";
    private static final String SHIP_TO_ADDRESS = "1200 Anastasia Ave, Suite 150";
    private static final UUID SHIP_TO_CITY_ID = UUID.fromString("86a42339-573b-408b-a029-ba6647c5d165");
    private static final String SHIP_TO_PHONE = "305-507-8496";
    private static final String SHIP_TO_CONTACT_NAME = "Juan Restrepo";
    private static final String SHIP_TO_EMAIL = "juan@itradingsolutions.com";

    private CityEntity shipToCity;

    @PostConstruct
    void init() {
        this.shipToCity = cityService.findEntityById(SHIP_TO_CITY_ID);
    }

    @Override
    @Transactional
    public IpPurchaseOrderDTO createIpPurchaseOrder(CreateIpPurchaseOrderRequest request) {
        var client = clientService.findClientById(request.clientId(), true);
        var quotation = resolveQuotation(request.quotationId());
        var supplier = resolveSupplier(request.supplierId(), quotation);

        var entity = new IpPurchaseOrderEntity();
        setBaseInfo(entity);
        setQuotationFields(entity, quotation);
        setSupplierFields(entity, supplier);
        setClientFields(entity, client);
        setShippingData(entity);
        entity.setNumber(generateConsecutive(client.getCode()));

        var saved = repository.save(entity);

        if (quotation != null) {
            completeQuoteRequestsLinkedToQuotation(quotation);
        }

        consecutiveService.saveConsecutive(CONSECUTIVE_MODULE, CONSECUTIVE_DEPARTMENT, saved.getNumber());
        return mapper.entityToDTO(saved);
    }

    @Override
    @Transactional
    public IpPurchaseOrderDTO updateIpPurchaseOrder(UUID id, UpdateIpPurchaseOrderRequest request) {
        var po = findEntityById(id);
        validateOpenPO(po, userService.getUserAuthenticated());
        validateEditable(po);
        validateAnsweredRestrictions(po, request);

        var oldNumber = po.getNumber();

        applyClientFields(po, request);
        applySupplierFields(po, request);
        applyFreeFields(po, request);
        applyPaymentTerms(po, request);

        var saved = repository.save(po);
        syncConsecutive(oldNumber, saved.getNumber());
        return mapper.entityToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public IpPurchaseOrderDTO findById(UUID id) {
        return mapper.entityToDTO(findEntityById(id));
    }

    @Override
    @Transactional
    public IpPurchaseOrderDTO cloneIpPurchaseOrder(UUID id) {
        var original = findEntityById(id);
        var user = userService.getUserAuthenticated();
        if (isOpenStatus(original.getStatus()))
            validateOpenPO(original, user);
        validateMaxOpenPo(user.getId());

        var clone = mapper.clone(original);
        clone.setStatus(IpPurchaseOrderStatus.CREATED);
        clone.setCreatedAt(ZonedDateTime.now(zoneId));
        clone.setOpenAt(ZonedDateTime.now(zoneId));
        clone.setOpenBy(user);
        clone.setSalesRep(user);
        clone.setNumber(generateConsecutive(clone.getClient().getCode()));
        clone.setPdfUrl(null);

        clone.setProducts(new ArrayList<>());
        clone.setOtherCharges(new ArrayList<>());
        clone.setImportedQuotationCharges(new ArrayList<>());
        clone.setImportedQuoteRequestCharges(new ArrayList<>());

        var now = ZonedDateTime.now();
        cloneChildren(original.getProducts(), clone.getProducts(), productMapper::clone,
                item -> item.setPurchaseOrder(clone), now);
        cloneChildren(original.getOtherCharges(), clone.getOtherCharges(), otherChargeMapper::clone,
                item -> item.setPurchaseOrder(clone), now);
        cloneChildren(original.getImportedQuotationCharges(), clone.getImportedQuotationCharges(),
                importedQChargeMapper::clone, item -> item.setPurchaseOrder(clone), now);
        cloneChildren(original.getImportedQuoteRequestCharges(), clone.getImportedQuoteRequestCharges(),
                importedQrChargeMapper::clone, item -> item.setPurchaseOrder(clone), now);

        var savedClone = repository.save(clone);
        consecutiveService.saveConsecutive(CONSECUTIVE_MODULE, CONSECUTIVE_DEPARTMENT, savedClone.getNumber());

        var clonedItem = new IpPurchaseOrdersClonedEntity();
        clonedItem.setId(original.getId(), savedClone.getId());
        clonedItem.setMainPurchaseOrder(original);
        clonedItem.setClonedPurchaseOrder(savedClone);
        clonedRepository.save(clonedItem);

        return mapper.entityToDTO(savedClone);
    }

    @Override
    @Transactional
    public IpPurchaseOrderDTO openAndLockIpPurchaseOrder(UUID id, OpenAndLockType type) {
        var po = findEntityById(id);
        if (po.getOpenBy() == null) {
            var user = userService.getUserAuthenticated();
            validateMaxOpenPo(user.getId());
            if (type.equals(OpenAndLockType.EDIT)) {
                po.setOpenBy(user);
                po.setOpenAt(ZonedDateTime.now(zoneId));
                po = repository.save(po);
            }
        }
        return mapper.entityToDTO(po);
    }

    @Override
    @Transactional
    public void unlockIpPurchaseOrder(UUID id) {
        repository.batchUnlockOpenBy(List.of(id));
    }

    @Override
    @Transactional
    public int batchUnlock(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        return repository.batchUnlockOpenBy(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<IpPurchaseOrderDTO> listAll(Pageable pageable, FilterListIpPurchaseOrder filters) {
        Specification<IpPurchaseOrderEntity> spec = (filters == null ? Specification.where(null) : filters.filter());
        Page<IpPurchaseOrderEntity> resp = repository.findAll(spec, pageable);
        return new PageImpl<>(
                resp.getContent().stream().map(mapper::entityToDTO).toList(),
                resp.getPageable(),
                resp.getTotalElements()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<IpPurchaseOrderDTO> listAllOpenByUser(String username) {
        return repository.fetchAllOpenByUsername(username).stream()
                .map(mapper::entityToDTO)
                .toList();
    }

    private IpQuotationEntity resolveQuotation(UUID quotationId) {
        if (quotationId == null) return null;
        return quotationRepository.findById(quotationId)
                .orElseThrow(() -> new NotExistIpPurchaseOrderException(simpleMessage("ip.q.not-exist")));
    }

    private SupplierEntity resolveSupplier(UUID supplierId, IpQuotationEntity quotation) {
        if (supplierId == null) {
            if (quotation != null)
                throw new InvalidSupplierForIpPurchaseOrderException(
                        compositeMessage("ip.po.supplier.required-for-quotation",
                                new String[]{quotation.getNumber()}));
            return null;
        }
        if (quotation != null)
            validateSupplierInQuotation(quotation, supplierId);
        return supplierService.findSupplierById(supplierId, true);
    }

    private void setBaseInfo(IpPurchaseOrderEntity entity) {
        var now = ZonedDateTime.now(zoneId);
        var user = userService.getUserAuthenticated();
        entity.setStatus(IpPurchaseOrderStatus.CREATED);
        entity.setCreatedAt(now);
        entity.setOpenAt(now);
        entity.setOpenBy(user);
        entity.setSalesRep(user);
    }

    private void setQuotationFields(IpPurchaseOrderEntity entity, IpQuotationEntity quotation) {
        if (quotation != null) {
            entity.setQuotation(quotation);
            entity.setCurrency(quotation.getCurrency());
            entity.setLeadTime(quotation.getLeadTime());
            entity.setLeadTimeType(quotation.getLeadTimeType());
        } else {
            entity.setCurrency(Currency.USD);
            entity.setLeadTime(0);
            entity.setLeadTimeType(LeadTime.DAYS);
        }
    }

    private void setSupplierFields(IpPurchaseOrderEntity entity, SupplierEntity supplier) {
        entity.setPaymentTerms(supplier != null ? supplier.getPaymentTerms() : null);
        if (supplier != null)
            entity.setSupplier(supplier);
    }

    private static void setClientFields(IpPurchaseOrderEntity entity, ClientEntity client) {
        entity.setClient(client);
    }

    private void setShippingData(IpPurchaseOrderEntity entity) {
        entity.setShipToName(SHIP_TO_NAME);
        entity.setShipToAddress(SHIP_TO_ADDRESS);
        entity.setShipToCity(shipToCity);
        entity.setShipToPhone(SHIP_TO_PHONE);
        entity.setShipToContactName(SHIP_TO_CONTACT_NAME);
        entity.setShipToEmail(SHIP_TO_EMAIL);
        entity.setSalesTax(BigDecimal.ZERO);
    }

    private String generateConsecutive(String clientCode) {
        return consecutiveService.generateConsecutive(CONSECUTIVE_MODULE, CONSECUTIVE_DEPARTMENT, clientCode);
    }

    private IpPurchaseOrderEntity findEntityById(UUID id) {
        return repository.findById(id).orElseThrow(
                () -> new NotExistIpPurchaseOrderException(simpleMessage("ip.po.not-exist")));
    }

    private void validateMaxOpenPo(UUID userId) {
        if (repository.countByOpenUserId(userId) >= maxTabsOpen)
            throw new NotOpenIpPoException(
                    compositeMessage("ip.po.not-open-max", new String[]{maxTabsOpen.toString()}));
    }

    private void validateOpenPO(IpPurchaseOrderEntity entity, UserEntity userAuthenticated) {
        if (entity.getOpenBy() == null)
            throw new NotOpenIpPurchaseOrderException(simpleMessage("ip.po.not-block"));
        if (!entity.getOpenBy().getId().equals(userAuthenticated.getId()))
            throw new NotOpenIpPurchaseOrderException(
                    compositeMessage("ip.po.not-block-by", new String[]{entity.getOpenBy().getFullName()}));
    }

    private static boolean isOpenStatus(IpPurchaseOrderStatus status) {
        return status == IpPurchaseOrderStatus.CREATED
                || status == IpPurchaseOrderStatus.SENT
                || status == IpPurchaseOrderStatus.ANSWERED;
    }

    private void validateEditable(IpPurchaseOrderEntity po) {
        Optional.of(po.getStatus())
                .filter(status -> !isOpenStatus(status))
                .ifPresent(status -> {
                    throw new IpPurchaseOrderNotEditableException(simpleMessage("ip.po.not-editable"));
                });
    }

    private void validateAnsweredRestrictions(IpPurchaseOrderEntity po, UpdateIpPurchaseOrderRequest request) {
        Optional.of(po)
                .filter(entity -> entity.getStatus() == IpPurchaseOrderStatus.ANSWERED)
                .filter(entity -> isChanged(request.clientId(), safeId(entity.getClient()))
                        || isChanged(request.supplierId(), safeId(entity.getSupplier()))
                        || isChanged(request.paymentTerms(), entity.getPaymentTerms()))
                .ifPresent(entity -> {
                    throw new IpPurchaseOrderAnsweredLockedException(
                            simpleMessage("ip.po.answered-fields-locked"));
                });
    }

    private void applyClientFields(IpPurchaseOrderEntity po, UpdateIpPurchaseOrderRequest request) {
        var hasQuotation = po.getQuotation() != null;

        Optional.ofNullable(request.clientId())
                .filter(clientId -> !hasQuotation)
                .filter(clientId -> !clientId.equals(po.getClient().getId()))
                .map(clientId -> clientService.findClientById(clientId, true))
                .ifPresent(client -> {
                    po.setClient(client);
                    po.setClientContact(null);
                    po.setNumber(generateConsecutive(client.getCode()));
                });

        Optional.ofNullable(request.currency())
                .filter(currency -> !hasQuotation)
                .ifPresent(po::setCurrency);
    }

    private void applySupplierFields(IpPurchaseOrderEntity po, UpdateIpPurchaseOrderRequest request) {
        Optional.ofNullable(po.getQuotation())
                .ifPresentOrElse(
                        quotation -> applySupplierWithQuotation(po, quotation, request),
                        () -> {
                            po.setSupplier(null);
                            po.setSupplierContact(null);
                            po.setSupplierPoNumber(null);
                        });
    }

    private void applySupplierWithQuotation(IpPurchaseOrderEntity po, IpQuotationEntity quotation,
                                            UpdateIpPurchaseOrderRequest request) {
        Optional.ofNullable(request.supplierId())
                .filter(supplierId -> !supplierId.equals(safeId(po.getSupplier())))
                .ifPresent(supplierId -> {
                    validateSupplierInQuotation(quotation, supplierId);
                    po.setSupplier(supplierService.findSupplierById(supplierId, true));
                });
        po.setSupplierContact(Optional.ofNullable(request.supplierContactId())
                .flatMap(contactId -> Optional.ofNullable(po.getSupplier())
                        .map(supplier -> supplierContactService.findById(contactId, supplier.getId())))
                .orElse(null));
        po.setSupplierPoNumber(Optional.ofNullable(po.getSupplier())
                .map(supplier -> request.supplierPoNumber())
                .orElse(null));
    }

    private void applyFreeFields(IpPurchaseOrderEntity po, UpdateIpPurchaseOrderRequest request) {
        po.setClientContact(Optional.ofNullable(request.clientContactId())
                .map(contactId -> clientContactService.findById(contactId, po.getClient().getId()))
                .orElse(null));
        po.setClientPoNumber(request.clientPoNumber());
        po.setShippingMethod(request.shippingMethod());
        Optional.ofNullable(request.salesRepId())
                .filter(salesRepId -> !salesRepId.equals(safeId(po.getSalesRep())))
                .ifPresent(salesRepId -> po.setSalesRep(userService.findEntityById(salesRepId, true)));
        po.setLeadTime(request.leadTime());
        po.setLeadTimeType(request.leadTimeType());
        po.setSalesTax(request.salesTax());
        po.setShipToName(request.shipToName());
        po.setShipToAddress(request.shipToAddress());
        Optional.ofNullable(request.shipToCityId())
                .filter(cityId -> !cityId.equals(po.getShipToCity().getId()))
                .ifPresent(cityId -> po.setShipToCity(cityService.findEntityById(cityId)));
        po.setShipToPhone(request.shipToPhone());
        po.setShipToContactName(request.shipToContactName());
        po.setShipToEmail(request.shipToEmail());
    }

    private void applyPaymentTerms(IpPurchaseOrderEntity po, UpdateIpPurchaseOrderRequest request) {
        Optional.of(po)
                .filter(entity -> entity.getStatus() != IpPurchaseOrderStatus.ANSWERED)
                .ifPresent(entity -> {
                    entity.setPaymentTerms(Optional.ofNullable(entity.getSupplier())
                            .map(SupplierEntity::getPaymentTerms)
                            .orElse(null));
                    Optional.ofNullable(request.paymentTerms())
                            .filter(terms -> validateAction(userService.getUserAuthenticated(),
                                    ModuleAction.EDIT_PAYMENT_TERMS_PURCHASE_ORDER))
                            .ifPresent(entity::setPaymentTerms);
                });
    }

    private void syncConsecutive(String oldNumber, String newNumber) {
        Optional.of(newNumber)
                .filter(number -> !number.equalsIgnoreCase(oldNumber))
                .ifPresent(number -> {
                    consecutiveService.saveConsecutive(CONSECUTIVE_MODULE, CONSECUTIVE_DEPARTMENT, number);
                    consecutiveService.deleteConsecutive(CONSECUTIVE_MODULE, CONSECUTIVE_DEPARTMENT, oldNumber);
                });
    }

    private static <T> boolean isChanged(T requested, T current) {
        return requested != null && !requested.equals(current);
    }

    private static UUID safeId(BaseEntity entity) {
        return entity != null ? entity.getId() : null;
    }

    private void validateSupplierInQuotation(IpQuotationEntity quotation, UUID supplierId) {
        var quoteRequests = quotation.getQuoteRequestsQuotations();
        if (quoteRequests == null || quoteRequests.isEmpty())
            throw new InvalidSupplierForIpPurchaseOrderException(
                    compositeMessage("ip.po.quotation-no-suppliers",
                            new String[]{quotation.getNumber()}));

        Set<UUID> validSupplierIds = new HashSet<>();
        quoteRequests.forEach(qqr -> {
            var qr = qqr.getQuoteRequest();
            if (qr != null && qr.getSupplier() != null)
                validSupplierIds.add(qr.getSupplier().getId());
        });

        if (validSupplierIds.isEmpty())
            throw new InvalidSupplierForIpPurchaseOrderException(
                    compositeMessage("ip.po.quotation-no-suppliers",
                            new String[]{quotation.getNumber()}));

        if (!validSupplierIds.contains(supplierId))
            throw new InvalidSupplierForIpPurchaseOrderException(
                    compositeMessage("ip.po.supplier.invalid-for-quotation",
                            new String[]{quotation.getNumber()}));
    }

    private static <T extends BaseEntity> void cloneChildren(List<T> source, List<T> target,
                                                              Function<T, T> cloner,
                                                              Consumer<T> linker, ZonedDateTime now) {
        if (source == null) return;
        source.forEach(item -> {
            var cloneItem = cloner.apply(item);
            linker.accept(cloneItem);
            cloneItem.setCreatedAt(now);
            target.add(cloneItem);
        });
    }

    private void completeQuoteRequestsLinkedToQuotation(IpQuotationEntity quotation) {
        var qrList = quotation.getQuoteRequestsQuotations();
        if (qrList == null || qrList.isEmpty()) return;

        qrList.stream()
                .map(IpQuotationsQuoteRequestEntity::getQuoteRequest)
                .filter(qr -> qr != null && qr.getStatus() == IpQuoteRequestStatus.ANSWERED)
                .forEach(qr -> qrService.changeStatusQuoteRequest(qr.getId(), IpQuoteRequestStatus.COMPLETE));
    }
}
