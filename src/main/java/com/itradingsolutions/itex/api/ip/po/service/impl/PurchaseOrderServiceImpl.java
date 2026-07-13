package com.itradingsolutions.itex.api.ip.po.service.impl;

import com.itradingsolutions.itex.api.common.models.entities.BaseEntity;
import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveDepartment;
import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveModule;
import com.itradingsolutions.itex.api.common.consecutive.services.IConsecutiveService;
import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.common.models.enums.OpenAndLockType;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.ip.po.exceptions.InvalidSupplierForQuotationException;
import com.itradingsolutions.itex.api.ip.po.exceptions.NotExistPurchaseOrderException;
import com.itradingsolutions.itex.api.ip.po.exceptions.NotOpenPoException;
import com.itradingsolutions.itex.api.ip.po.exceptions.NotOpenPurchaseOrderException;
import com.itradingsolutions.itex.api.ip.po.models.dto.PurchaseOrderDTO;
import com.itradingsolutions.itex.api.ip.po.models.entities.PurchaseOrderEntity;
import com.itradingsolutions.itex.api.ip.po.models.entities.PurchaseOrdersClonedEntity;
import com.itradingsolutions.itex.api.ip.po.models.enums.PurchaseOrderStatus;
import com.itradingsolutions.itex.api.ip.po.models.mapper.PurchaseOrderMapper;
import com.itradingsolutions.itex.api.ip.po.models.mapper.PurchaseOrderOtherChargeMapper;
import com.itradingsolutions.itex.api.ip.po.models.mapper.PurchaseOrderOtherChargesQuotationMapper;
import com.itradingsolutions.itex.api.ip.po.models.mapper.PurchaseOrderOtherChargesQuotationQrMapper;
import com.itradingsolutions.itex.api.ip.po.models.mapper.PurchaseOrderProductMapper;
import com.itradingsolutions.itex.api.ip.po.models.request.CreatePurchaseOrderRequest;
import com.itradingsolutions.itex.api.ip.po.repository.IPurchaseOrderRepository;
import com.itradingsolutions.itex.api.ip.po.repository.IPurchaseOrdersClonedRepository;
import com.itradingsolutions.itex.api.ip.po.service.IPurchaseOrderService;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationEntity;
import com.itradingsolutions.itex.api.ip.q.repository.IpQuotationRepository;
import com.itradingsolutions.itex.api.masters.location.models.entities.CityEntity;
import com.itradingsolutions.itex.api.masters.location.services.ICityService;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientEntity;
import com.itradingsolutions.itex.api.partners.clients.services.IClientService;
import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierEntity;
import com.itradingsolutions.itex.api.partners.suppliers.services.ISupplierService;
import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl extends UtilServiceAbs implements IPurchaseOrderService {

    private final IPurchaseOrderRepository repository;
    private final PurchaseOrderMapper mapper;
    private final IConsecutiveService consecutiveService;
    private final IUserService userService;
    private final IClientService clientService;
    private final ISupplierService supplierService;
    private final ICityService cityService;
    private final IpQuotationRepository quotationRepository;
    private final IPurchaseOrdersClonedRepository clonedRepository;
    private final PurchaseOrderProductMapper productMapper;
    private final PurchaseOrderOtherChargeMapper otherChargeMapper;
    private final PurchaseOrderOtherChargesQuotationMapper importedQChargeMapper;
    private final PurchaseOrderOtherChargesQuotationQrMapper importedQrChargeMapper;

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
    public PurchaseOrderDTO createPurchaseOrder(CreatePurchaseOrderRequest request) {
        var client = clientService.findClientById(request.clientId(), true);
        var quotation = resolveQuotation(request.quotationId());
        var supplier = resolveSupplier(request.supplierId(), quotation);

        var entity = new PurchaseOrderEntity();
        setBaseInfo(entity);
        setQuotationFields(entity, quotation);
        setSupplierFields(entity, supplier);
        setClientFields(entity, client);
        setShippingData(entity);
        entity.setNumber(generateConsecutive(client.getCode()));

        var saved = repository.save(entity);
        consecutiveService.saveConsecutive(CONSECUTIVE_MODULE, CONSECUTIVE_DEPARTMENT, saved.getNumber());
        return mapper.entityToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrderDTO findById(UUID id) {
        return mapper.entityToDTO(findEntityById(id));
    }

    @Override
    @Transactional
    public PurchaseOrderDTO clonePurchaseOrder(UUID id) {
        var original = findEntityById(id);
        var user = userService.getUserAuthenticated();
        if (isOpenStatus(original.getStatus()))
            validateOpenPO(original, user);
        validateMaxOpenPo(user.getId());

        var clone = mapper.clone(original);
        clone.setStatus(PurchaseOrderStatus.CREATED);
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

        var clonedItem = new PurchaseOrdersClonedEntity();
        clonedItem.setId(original.getId(), savedClone.getId());
        clonedItem.setMainPurchaseOrder(original);
        clonedItem.setClonedPurchaseOrder(savedClone);
        clonedRepository.save(clonedItem);

        return mapper.entityToDTO(savedClone);
    }

    @Override
    @Transactional
    public PurchaseOrderDTO openAndLockPurchaseOrder(UUID id, OpenAndLockType type) {
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
    public void unlockPurchaseOrder(UUID id) {
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
    public List<PurchaseOrderDTO> listAllOpenByUser(String username) {
        return repository.fetchAllOpenByUsername(username).stream()
                .map(mapper::entityToDTO)
                .toList();
    }

    private IpQuotationEntity resolveQuotation(UUID quotationId) {
        if (quotationId == null) return null;
        return quotationRepository.findById(quotationId)
                .orElseThrow(() -> new NotExistPurchaseOrderException(simpleMessage("ip.q.not-exist")));
    }

    private SupplierEntity resolveSupplier(UUID supplierId, IpQuotationEntity quotation) {
        if (supplierId == null) {
            if (quotation != null)
                throw new InvalidSupplierForQuotationException(
                        compositeMessage("ip.po.supplier.required-for-quotation",
                                new String[]{quotation.getNumber()}));
            return null;
        }
        if (quotation != null)
            validateSupplierInQuotation(quotation, supplierId);
        return supplierService.findSupplierById(supplierId, true);
    }

    private void setBaseInfo(PurchaseOrderEntity entity) {
        var now = ZonedDateTime.now(zoneId);
        var user = userService.getUserAuthenticated();
        entity.setStatus(PurchaseOrderStatus.CREATED);
        entity.setCreatedAt(now);
        entity.setOpenAt(now);
        entity.setOpenBy(user);
        entity.setSalesRep(user);
    }

    private void setQuotationFields(PurchaseOrderEntity entity, IpQuotationEntity quotation) {
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

    private void setSupplierFields(PurchaseOrderEntity entity, SupplierEntity supplier) {
        entity.setPaymentTerms(supplier != null ? supplier.getPaymentTerms() : null);
        if (supplier != null)
            entity.setSupplier(supplier);
    }

    private static void setClientFields(PurchaseOrderEntity entity, ClientEntity client) {
        entity.setClient(client);
    }

    private void setShippingData(PurchaseOrderEntity entity) {
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

    private PurchaseOrderEntity findEntityById(UUID id) {
        return repository.findById(id).orElseThrow(
                () -> new NotExistPurchaseOrderException(simpleMessage("ip.po.not-exist")));
    }

    private void validateMaxOpenPo(UUID userId) {
        if (repository.countByOpenUserId(userId) >= maxTabsOpen)
            throw new NotOpenPoException(
                    compositeMessage("ip.po.not-open-max", new String[]{maxTabsOpen.toString()}));
    }

    private void validateOpenPO(PurchaseOrderEntity entity, UserEntity userAuthenticated) {
        if (entity.getOpenBy() == null)
            throw new NotOpenPurchaseOrderException(simpleMessage("ip.po.not-block"));
        if (!entity.getOpenBy().getId().equals(userAuthenticated.getId()))
            throw new NotOpenPurchaseOrderException(
                    compositeMessage("ip.po.not-block-by", new String[]{entity.getOpenBy().getFullName()}));
    }

    private static boolean isOpenStatus(PurchaseOrderStatus status) {
        return status == PurchaseOrderStatus.CREATED
                || status == PurchaseOrderStatus.SENT
                || status == PurchaseOrderStatus.ANSWERED;
    }

    private void validateSupplierInQuotation(IpQuotationEntity quotation, UUID supplierId) {
        var quoteRequests = quotation.getQuoteRequestsQuotations();
        if (quoteRequests == null || quoteRequests.isEmpty())
            throw new InvalidSupplierForQuotationException(
                    compositeMessage("ip.po.quotation-no-suppliers",
                            new String[]{quotation.getNumber()}));

        Set<UUID> validSupplierIds = new HashSet<>();
        quoteRequests.forEach(qqr -> {
            var qr = qqr.getQuoteRequest();
            if (qr != null && qr.getSupplier() != null)
                validSupplierIds.add(qr.getSupplier().getId());
        });

        if (validSupplierIds.isEmpty())
            throw new InvalidSupplierForQuotationException(
                    compositeMessage("ip.po.quotation-no-suppliers",
                            new String[]{quotation.getNumber()}));

        if (!validSupplierIds.contains(supplierId))
            throw new InvalidSupplierForQuotationException(
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
}
