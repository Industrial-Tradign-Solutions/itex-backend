package com.itradingsolutions.itex.api.ip.po.service.impl;

import com.itradingsolutions.itex.api.admin.user.models.dto.UserDTO;
import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;
import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.common.service.impl.HistoryServiceImpl;
import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;
import com.itradingsolutions.itex.api.ip.po.models.dto.PurchaseOrderDTO;
import com.itradingsolutions.itex.api.ip.po.models.dto.PurchaseOrderHistoryDTO;
import com.itradingsolutions.itex.api.ip.po.models.dto.PurchaseOrderOtherChargeDTO;
import com.itradingsolutions.itex.api.ip.po.models.dto.PurchaseOrderOtherChargesQuotationDTO;
import com.itradingsolutions.itex.api.ip.po.models.dto.PurchaseOrderOtherChargesQuotationQrDTO;
import com.itradingsolutions.itex.api.ip.po.models.dto.PurchaseOrderProductDTO;
import com.itradingsolutions.itex.api.ip.po.models.entities.PurchaseOrderHistoryEntity;
import com.itradingsolutions.itex.api.ip.po.models.enums.PurchaseOrderHistoryAction;
import com.itradingsolutions.itex.api.ip.po.models.enums.PurchaseOrderStatus;
import com.itradingsolutions.itex.api.ip.po.models.mapper.PurchaseOrderHistoryMapper;
import com.itradingsolutions.itex.api.ip.po.repository.IPurchaseOrderHistoryRepository;
import com.itradingsolutions.itex.api.ip.po.service.IPurchaseOrderHistoryService;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationDTO;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationProductDTO;
import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientContactDTO;
import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientDTO;
import com.itradingsolutions.itex.api.partners.suppliers.models.dto.SupplierContactDTO;
import com.itradingsolutions.itex.api.partners.suppliers.models.dto.SupplierDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseOrderHistoryServiceImpl extends HistoryServiceImpl implements IPurchaseOrderHistoryService {

    private final IPurchaseOrderHistoryRepository repository;
    private final PurchaseOrderHistoryMapper mapper;

    @Override
    @Transactional
    public void addHistory(PurchaseOrderHistoryAction action, PurchaseOrderDTO oldDto, PurchaseOrderDTO newDto) {
        validateNotNull(newDto, "Data is not null");
        var entity = new PurchaseOrderHistoryEntity();
        entity.setIpPurchaseOrder(newDto.getId());
        entity.setData(resolveHistoryData(action, oldDto, newDto));
        addHistoryCommon(action, entity);
    }

    @Override
    @Transactional
    public void addHistoryProduct(PurchaseOrderHistoryAction action, PurchaseOrderProductDTO oldDto, PurchaseOrderProductDTO newDto, UUID poId) {
        validateNotNull(newDto, "Data is not null");
        var entity = new PurchaseOrderHistoryEntity();
        entity.setIpPurchaseOrder(poId);
        entity.setData(resolveHistoryProductData(action, oldDto, newDto));
        addHistoryCommon(action, entity);
    }

    @Override
    @Transactional
    public void addHistoryOtherCharge(PurchaseOrderHistoryAction action, PurchaseOrderOtherChargeDTO oldDto, PurchaseOrderOtherChargeDTO newDto, UUID poId) {
        validateNotNull(newDto, "Data is not null");
        var entity = new PurchaseOrderHistoryEntity();
        entity.setIpPurchaseOrder(poId);
        entity.setData(resolveHistoryOtherChargeData(action, oldDto, newDto));
        addHistoryCommon(action, entity);
    }

    @Override
    @Transactional
    public void addHistoryImportedQCharge(PurchaseOrderHistoryAction action, PurchaseOrderOtherChargesQuotationDTO oldDto, PurchaseOrderOtherChargesQuotationDTO newDto, UUID poId) {
        validateNotNull(newDto, "Data is not null");
        var entity = new PurchaseOrderHistoryEntity();
        entity.setIpPurchaseOrder(poId);
        entity.setData(resolveHistoryImportedQChargeData(action, oldDto, newDto));
        addHistoryCommon(action, entity);
    }

    @Override
    @Transactional
    public void addHistoryImportedQrCharge(PurchaseOrderHistoryAction action, PurchaseOrderOtherChargesQuotationQrDTO oldDto, PurchaseOrderOtherChargesQuotationQrDTO newDto, UUID poId) {
        validateNotNull(newDto, "Data is not null");
        var entity = new PurchaseOrderHistoryEntity();
        entity.setIpPurchaseOrder(poId);
        entity.setData(resolveHistoryImportedQrChargeData(action, oldDto, newDto));
        addHistoryCommon(action, entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrderHistoryDTO> getHistoryById(UUID id) {
        var list = repository.fetchByPoId(id);
        return list.stream().map(mapper::entityToDTO).toList();
    }

    private void addHistoryCommon(PurchaseOrderHistoryAction action, PurchaseOrderHistoryEntity entity) {
        entity.setUser(getUserAuthUser());
        entity.setAction(action);
        if (isUpdateAction(action)) {
            if (!entity.getData().isEmpty())
                repository.save(entity);
        } else {
            repository.save(entity);
        }
    }

    private boolean isUpdateAction(PurchaseOrderHistoryAction action) {
        return action == PurchaseOrderHistoryAction.UPDATE
            || action == PurchaseOrderHistoryAction.UPDATE_PRODUCT
            || action == PurchaseOrderHistoryAction.UPDATE_OTHER_CHARGE
            || action == PurchaseOrderHistoryAction.STATUS_CHANGE;
    }

    private Map<String, Object> resolveHistoryData(PurchaseOrderHistoryAction action, PurchaseOrderDTO oldDto, PurchaseOrderDTO newDto) {
        return switch (action) {
            case CREATE, REJECTED -> convertToMap(newDto, true, true);
            case UPDATE -> {
                validateNotNull(oldDto, "oldDto must not be null for UPDATE");
                yield getValidateChanges(oldDto, newDto);
            }
            case CLONE -> {
                validateNotNull(oldDto, "oldDto must not be null for Clone");
                yield getValidateChangesClone(oldDto);
            }
            case STATUS_CHANGE -> {
                validateNotNull(oldDto, "oldDto must not be null for STATUS_CHANGE");
                yield getValidateChangesStatus(oldDto, newDto);
            }
            default -> throw new BadRequestException("Invalid action");
        };
    }

    private Map<String, Object> resolveHistoryProductData(PurchaseOrderHistoryAction action, PurchaseOrderProductDTO oldDto, PurchaseOrderProductDTO newDto) {
        return switch (action) {
            case ADD_PRODUCT, REMOVE_PRODUCT -> convertToMap(newDto, true, true);
            case UPDATE_PRODUCT -> {
                validateNotNull(oldDto, "oldDto must not be null for UPDATE_PRODUCT");
                yield getValidateChangesProduct(oldDto, newDto);
            }
            default -> throw new BadRequestException("Invalid action");
        };
    }

    private Map<String, Object> resolveHistoryOtherChargeData(PurchaseOrderHistoryAction action, PurchaseOrderOtherChargeDTO oldDto, PurchaseOrderOtherChargeDTO newDto) {
        return switch (action) {
            case ADD_OTHER_CHARGE, REMOVE_OTHER_CHARGE -> convertToMap(newDto, true, true);
            case UPDATE_OTHER_CHARGE -> {
                validateNotNull(oldDto, "oldDto must not be null for UPDATE_OTHER_CHARGE");
                yield getValidateChangesOtherCharge(oldDto, newDto);
            }
            default -> throw new BadRequestException("Invalid action");
        };
    }

    private Map<String, Object> resolveHistoryImportedQChargeData(PurchaseOrderHistoryAction action, PurchaseOrderOtherChargesQuotationDTO oldDto, PurchaseOrderOtherChargesQuotationDTO newDto) {
        return switch (action) {
            case ADD_IMPORTED_Q_CHARGE, REMOVE_IMPORTED_Q_CHARGE -> convertToMap(newDto, true, true);
            default -> throw new BadRequestException("Invalid action");
        };
    }

    private Map<String, Object> resolveHistoryImportedQrChargeData(PurchaseOrderHistoryAction action, PurchaseOrderOtherChargesQuotationQrDTO oldDto, PurchaseOrderOtherChargesQuotationQrDTO newDto) {
        return switch (action) {
            case ADD_IMPORTED_QR_CHARGE, REMOVE_IMPORTED_QR_CHARGE -> convertToMap(newDto, true, true);
            default -> throw new BadRequestException("Invalid action");
        };
    }

    private Map<String, Object> getValidateChanges(PurchaseOrderDTO oldDto, PurchaseOrderDTO newDto) {
        Map<String, Object> changes = new HashMap<>();

        List<String> simpleFields = Arrays.asList(
            "number", "clientPoNumber", "supplierPoNumber",
            "shippingMethod", "remarks", "internalRemarks",
            "shipToName", "shipToAddress", "shipToPhone",
            "shipToContactName", "shipToEmail", "pdfUrl",
            "leadTime", "openAt", "sentAt", "answeredAt", "completeAt", "rejectAt"
        );

        simpleFields.forEach(field ->
            putIfChanged(changes, field, getFieldValue(oldDto, field), getFieldValue(newDto, field))
        );

        putIfChangedBigDecimal(changes, "salesTax", oldDto.getSalesTax(), newDto.getSalesTax());

        putIfChanged(changes, "status",
            safeGet(oldDto.getStatus(), PurchaseOrderStatus::getName),
            safeGet(newDto.getStatus(), PurchaseOrderStatus::getName)
        );
        putIfChanged(changes, "currency",
            safeGet(oldDto.getCurrency(), Currency::getName),
            safeGet(newDto.getCurrency(), Currency::getName)
        );
        putIfChanged(changes, "paymentTerms",
            safeGet(oldDto.getPaymentTerms(), PaymentTerms::getName),
            safeGet(newDto.getPaymentTerms(), PaymentTerms::getName)
        );
        putIfChanged(changes, "leadTimeType",
            safeGet(oldDto.getLeadTimeType(), LeadTime::getName),
            safeGet(newDto.getLeadTimeType(), LeadTime::getName)
        );

        compareOther(changes, "quotation",
            safeGet(oldDto.getQuotation(), IpQuotationDTO::getId),
            safeGet(newDto.getQuotation(), IpQuotationDTO::getId),
            safeGet(oldDto.getQuotation(), IpQuotationDTO::getName),
            safeGet(newDto.getQuotation(), IpQuotationDTO::getName)
        );
        compareOther(changes, "client",
            safeGet(oldDto.getClient(), ClientDTO::getId),
            safeGet(newDto.getClient(), ClientDTO::getId),
            safeGet(oldDto.getClient(), ClientDTO::getCode),
            safeGet(newDto.getClient(), ClientDTO::getCode)
        );
        compareOther(changes, "clientContact",
            safeGet(oldDto.getClientContact(), ClientContactDTO::getId),
            safeGet(newDto.getClientContact(), ClientContactDTO::getId),
            safeGet(oldDto.getClientContact(), ClientContactDTO::getName),
            safeGet(newDto.getClientContact(), ClientContactDTO::getName)
        );
        compareOther(changes, "salesRep",
            safeGet(oldDto.getSalesRep(), UserDTO::getId),
            safeGet(newDto.getSalesRep(), UserDTO::getId),
            safeGet(oldDto.getSalesRep(), UserDTO::getFullName),
            safeGet(newDto.getSalesRep(), UserDTO::getFullName)
        );
        compareOther(changes, "supplier",
            safeGet(oldDto.getSupplier(), SupplierDTO::getId),
            safeGet(newDto.getSupplier(), SupplierDTO::getId),
            safeGet(oldDto.getSupplier(), SupplierDTO::getName),
            safeGet(newDto.getSupplier(), SupplierDTO::getName)
        );
        compareOther(changes, "supplierContact",
            safeGet(oldDto.getSupplierContact(), SupplierContactDTO::getId),
            safeGet(newDto.getSupplierContact(), SupplierContactDTO::getId),
            safeGet(oldDto.getSupplierContact(), SupplierContactDTO::getName),
            safeGet(newDto.getSupplierContact(), SupplierContactDTO::getName)
        );

        compareMaster(changes, "shipToCity",
            oldDto.getShipToCity(),
            newDto.getShipToCity()
        );

        return changes;
    }

    private Map<String, Object> getValidateChangesProduct(PurchaseOrderProductDTO oldDto, PurchaseOrderProductDTO newDto) {
        Map<String, Object> changes = new HashMap<>();
        changes.put("poProdId", oldDto.getId());

        putIfChanged(changes, "number",
            getFieldValue(oldDto, "number"),
            getFieldValue(newDto, "number")
        );

        compareOther(changes, "quotationProduct",
            safeGet(oldDto.getQuotationProduct(), BaseDTO::getId),
            safeGet(newDto.getQuotationProduct(), BaseDTO::getId),
            safeGet(oldDto.getQuotationProduct(), IpQuotationProductDTO::getQrNumber),
            safeGet(newDto.getQuotationProduct(), IpQuotationProductDTO::getQrNumber)
        );

        return changes;
    }

    private Map<String, Object> getValidateChangesOtherCharge(PurchaseOrderOtherChargeDTO oldDto, PurchaseOrderOtherChargeDTO newDto) {
        Map<String, Object> changes = new HashMap<>();
        changes.put("poOtherChargeId", oldDto.getId());

        putIfChanged(changes, "description",
            getFieldValue(oldDto, "description"),
            getFieldValue(newDto, "description")
        );
        putIfChangedBigDecimal(changes, "value", oldDto.getValue(), newDto.getValue());

        return changes;
    }

    private Map<String, Object> getValidateChangesClone(PurchaseOrderDTO oldDto) {
        Map<String, Object> changes = new HashMap<>();
        changes.put("clonedBy", oldDto.getNumber());
        return changes;
    }

    private Map<String, Object> getValidateChangesStatus(PurchaseOrderDTO oldDto, PurchaseOrderDTO newDto) {
        Map<String, Object> changes = new HashMap<>();

        putIfChanged(changes, "status",
            safeGet(oldDto.getStatus(), PurchaseOrderStatus::getName),
            safeGet(newDto.getStatus(), PurchaseOrderStatus::getName)
        );

        List<String> timestampFields = Arrays.asList(
            "openAt", "sentAt", "answeredAt", "completeAt", "rejectAt"
        );

        timestampFields.forEach(field ->
            putIfChanged(changes, field, getFieldValue(oldDto, field), getFieldValue(newDto, field))
        );

        return changes;
    }
}
