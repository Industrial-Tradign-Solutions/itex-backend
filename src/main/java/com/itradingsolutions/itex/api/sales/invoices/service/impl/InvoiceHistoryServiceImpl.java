package com.itradingsolutions.itex.api.sales.invoices.service.impl;

import com.itradingsolutions.itex.api.admin.user.models.dto.UserDTO;
import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;
import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.common.service.impl.HistoryServiceImpl;
import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.common.util.models.enums.Incoterms;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;
import com.itradingsolutions.itex.api.ip.products.models.enums.ProductCondition;
import com.itradingsolutions.itex.api.masters.location.models.dto.CityDTO;
import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientContactDTO;
import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceChargeDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceHistoryDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoicePaymentDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceProductDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.dto.InvoiceTaxDTO;
import com.itradingsolutions.itex.api.sales.invoices.models.entities.InvoiceHistoryEntity;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceChargeType;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceHistoryAction;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceStatus;
import com.itradingsolutions.itex.api.sales.invoices.models.enums.InvoiceTaxType;
import com.itradingsolutions.itex.api.sales.invoices.models.mapper.InvoiceHistoryMapper;
import com.itradingsolutions.itex.api.sales.invoices.repository.IInvoiceHistoryRepository;
import com.itradingsolutions.itex.api.sales.invoices.service.IInvoiceHistoryService;
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
public class InvoiceHistoryServiceImpl extends HistoryServiceImpl implements IInvoiceHistoryService {

    private final IInvoiceHistoryRepository repository;
    private final InvoiceHistoryMapper mapper;

    @Override
    @Transactional
    public void addHistory(InvoiceHistoryAction action, InvoiceDTO oldDto, InvoiceDTO newDto) {
        validateNotNull(newDto, "Data is not null");
        var entity = new InvoiceHistoryEntity();
        entity.setInvoice(newDto.getId());
        entity.setData(resolveHistoryData(action, oldDto, newDto));
        addHistoryCommon(action, entity);
    }

    @Override
    @Transactional
    public void addHistoryProduct(InvoiceHistoryAction action, InvoiceProductDTO oldDto, InvoiceProductDTO newDto, UUID invoiceId) {
        validateNotNull(newDto != null ? newDto : oldDto, "Data is not null");
        var entity = new InvoiceHistoryEntity();
        entity.setInvoice(invoiceId);
        entity.setData(resolveHistoryProductData(action, oldDto, newDto));
        addHistoryCommon(action, entity);
    }

    @Override
    @Transactional
    public void addHistoryCharge(InvoiceHistoryAction action, InvoiceChargeDTO oldDto, InvoiceChargeDTO newDto, UUID invoiceId) {
        validateNotNull(newDto != null ? newDto : oldDto, "Data is not null");
        var entity = new InvoiceHistoryEntity();
        entity.setInvoice(invoiceId);
        entity.setData(resolveHistoryChargeData(action, oldDto, newDto));
        addHistoryCommon(action, entity);
    }

    @Override
    @Transactional
    public void addHistoryTax(InvoiceHistoryAction action, InvoiceTaxDTO oldDto, InvoiceTaxDTO newDto, UUID invoiceId) {
        validateNotNull(newDto != null ? newDto : oldDto, "Data is not null");
        var entity = new InvoiceHistoryEntity();
        entity.setInvoice(invoiceId);
        entity.setData(resolveHistoryTaxData(action, oldDto, newDto));
        addHistoryCommon(action, entity);
    }

    @Override
    @Transactional
    public void addHistoryPayment(InvoiceHistoryAction action, InvoicePaymentDTO oldDto, InvoicePaymentDTO newDto, UUID invoiceId) {
        validateNotNull(newDto != null ? newDto : oldDto, "Data is not null");
        var entity = new InvoiceHistoryEntity();
        entity.setInvoice(invoiceId);
        entity.setData(resolveHistoryPaymentData(action, oldDto, newDto));
        addHistoryCommon(action, entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceHistoryDTO> getHistoryById(UUID invoiceId) {
        var list = repository.fetchByInvoiceId(invoiceId);
        return list.stream().map(mapper::entityToDTO).toList();
    }

    private void addHistoryCommon(InvoiceHistoryAction action, InvoiceHistoryEntity entity) {
        entity.setUser(getUserAuthUser());
        entity.setAction(action);
        if (isUpdateAction(action)) {
            if (!entity.getData().isEmpty())
                repository.save(entity);
        } else {
            repository.save(entity);
        }
    }

    private boolean isUpdateAction(InvoiceHistoryAction action) {
        return action == InvoiceHistoryAction.UPDATE
            || action == InvoiceHistoryAction.ISSUE
            || action == InvoiceHistoryAction.CANCEL
            || action == InvoiceHistoryAction.REVERT_TO_DRAFT
            || action == InvoiceHistoryAction.UPDATE_PRODUCT
            || action == InvoiceHistoryAction.UPDATE_CHARGE
            || action == InvoiceHistoryAction.UPDATE_TAX
            || action == InvoiceHistoryAction.VOID_PAYMENT;
    }

    private Map<String, Object> resolveHistoryData(InvoiceHistoryAction action, InvoiceDTO oldDto, InvoiceDTO newDto) {
        return switch (action) {
            case CREATE -> convertToMap(newDto, true, true);
            case UPDATE -> {
                validateNotNull(oldDto, "oldDto must not be null for " + action);
                yield getValidateChanges(oldDto, newDto);
            }
            case CLONE -> {
                validateNotNull(oldDto, "oldDto must not be null for Clone");
                yield getValidateChangesClone(oldDto);
            }
            case ISSUE, CANCEL, REVERT_TO_DRAFT -> {
                validateNotNull(oldDto, "oldDto must not be null for " + action);
                yield getValidateChangesStatus(oldDto, newDto);
            }
            default -> throw new BadRequestException("Invalid action");
        };
    }

    private Map<String, Object> resolveHistoryProductData(InvoiceHistoryAction action, InvoiceProductDTO oldDto, InvoiceProductDTO newDto) {
        return switch (action) {
            case ADD_PRODUCT -> convertToMap(newDto, true, true);
            case REMOVE_PRODUCT -> convertToMap(oldDto, true, true);
            case UPDATE_PRODUCT -> {
                validateNotNull(oldDto, "oldDto must not be null for UPDATE_PRODUCT");
                yield getValidateChangesProduct(oldDto, newDto);
            }
            default -> throw new BadRequestException("Invalid action");
        };
    }

    private Map<String, Object> resolveHistoryChargeData(InvoiceHistoryAction action, InvoiceChargeDTO oldDto, InvoiceChargeDTO newDto) {
        return switch (action) {
            case ADD_CHARGE -> convertToMap(newDto, true, true);
            case REMOVE_CHARGE -> convertToMap(oldDto, true, true);
            case UPDATE_CHARGE -> {
                validateNotNull(oldDto, "oldDto must not be null for UPDATE_CHARGE");
                yield getValidateChangesCharge(oldDto, newDto);
            }
            default -> throw new BadRequestException("Invalid action");
        };
    }

    private Map<String, Object> resolveHistoryTaxData(InvoiceHistoryAction action, InvoiceTaxDTO oldDto, InvoiceTaxDTO newDto) {
        return switch (action) {
            case ADD_TAX -> convertToMap(newDto, true, true);
            case REMOVE_TAX -> convertToMap(oldDto, true, true);
            case UPDATE_TAX -> {
                validateNotNull(oldDto, "oldDto must not be null for UPDATE_TAX");
                yield getValidateChangesTax(oldDto, newDto);
            }
            default -> throw new BadRequestException("Invalid action");
        };
    }

    private Map<String, Object> resolveHistoryPaymentData(InvoiceHistoryAction action, InvoicePaymentDTO oldDto, InvoicePaymentDTO newDto) {
        return switch (action) {
            case REGISTER_PAYMENT -> convertToMap(newDto, true, true);
            case VOID_PAYMENT -> {
                validateNotNull(oldDto, "oldDto must not be null for VOID_PAYMENT");
                yield getValidateChangesPayment(oldDto, newDto);
            }
            default -> throw new BadRequestException("Invalid action");
        };
    }

    private Map<String, Object> getValidateChanges(InvoiceDTO oldDto, InvoiceDTO newDto) {
        Map<String, Object> changes = new HashMap<>();

        List<String> simpleFields = Arrays.asList(
            "orderNumber", "via", "awbBl", "remarks", "internalRemarks",
            "packingList", "shipToName", "shipToAddress", "shipToPhone",
            "shipToContactName", "shipToEmail", "pdfUrl"
        );

        simpleFields.forEach(field ->
            putIfChanged(changes, field, getFieldValue(oldDto, field), getFieldValue(newDto, field))
        );

        putIfChangedBigDecimal(changes, "totalAmount", oldDto.getTotalAmount(), newDto.getTotalAmount());
        putIfChangedBigDecimal(changes, "paidAmount", oldDto.getPaidAmount(), newDto.getPaidAmount());

        putIfChanged(changes, "currency",
            safeGet(oldDto.getCurrency(), Currency::getName),
            safeGet(newDto.getCurrency(), Currency::getName)
        );
        putIfChanged(changes, "incoterms",
            safeGet(oldDto.getIncoterms(), Incoterms::getName),
            safeGet(newDto.getIncoterms(), Incoterms::getName)
        );
        putIfChanged(changes, "paymentTerms",
            safeGet(oldDto.getPaymentTerms(), PaymentTerms::getName),
            safeGet(newDto.getPaymentTerms(), PaymentTerms::getName)
        );

        if (oldDto.isOverdue() != newDto.isOverdue()) {
            changes.put("overdue", getChangeItem(oldDto.isOverdue(), newDto.isOverdue()));
        }

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
        compareOther(changes, "openBy",
            safeGet(oldDto.getOpenBy(), UserDTO::getId),
            safeGet(newDto.getOpenBy(), UserDTO::getId),
            safeGet(oldDto.getOpenBy(), UserDTO::getFullName),
            safeGet(newDto.getOpenBy(), UserDTO::getFullName)
        );

        compareMaster(changes, "shipToCity",
            oldDto.getShipToCity(),
            newDto.getShipToCity()
        );

        return changes;
    }

    private Map<String, Object> getValidateChangesStatus(InvoiceDTO oldDto, InvoiceDTO newDto) {
        Map<String, Object> changes = new HashMap<>();

        putIfChanged(changes, "status",
            safeGet(oldDto.getStatus(), InvoiceStatus::getName),
            safeGet(newDto.getStatus(), InvoiceStatus::getName)
        );

        List<String> timestampFields = Arrays.asList(
            "dueAt", "issuedAt", "cancelledAt", "cancelReason", "overdueNotifiedAt"
        );

        timestampFields.forEach(field ->
            putIfChanged(changes, field, getFieldValue(oldDto, field), getFieldValue(newDto, field))
        );

        return changes;
    }

    private Map<String, Object> getValidateChangesClone(InvoiceDTO oldDto) {
        Map<String, Object> changes = new HashMap<>();
        changes.put("clonedBy", oldDto.getName());
        return changes;
    }

    private Map<String, Object> getValidateChangesProduct(InvoiceProductDTO oldDto, InvoiceProductDTO newDto) {
        Map<String, Object> changes = new HashMap<>();

        List<String> simpleFields = Arrays.asList("number", "unitType", "leadTime");

        simpleFields.forEach(field ->
            putIfChanged(changes, field, getFieldValue(oldDto, field), getFieldValue(newDto, field))
        );

        putIfChangedBigDecimal(changes, "quantity", oldDto.getQuantity(), newDto.getQuantity());
        putIfChangedBigDecimal(changes, "unitPrice", oldDto.getUnitPrice(), newDto.getUnitPrice());
        putIfChangedBigDecimal(changes, "profitMargin", oldDto.getProfitMargin(), newDto.getProfitMargin());

        putIfChanged(changes, "leadTimeType",
            safeGet(oldDto.getLeadTimeType(), LeadTime::getName),
            safeGet(newDto.getLeadTimeType(), LeadTime::getName)
        );
        putIfChanged(changes, "condition",
            safeGet(oldDto.getCondition(), ProductCondition::getName),
            safeGet(newDto.getCondition(), ProductCondition::getName)
        );

        compareOther(changes, "product",
            safeGet(oldDto.getProduct(), BaseDTO::getId),
            safeGet(newDto.getProduct(), BaseDTO::getId),
            safeGet(oldDto.getProduct(), p -> p.getDescription()),
            safeGet(newDto.getProduct(), p -> p.getDescription())
        );

        return changes;
    }

    private Map<String, Object> getValidateChangesCharge(InvoiceChargeDTO oldDto, InvoiceChargeDTO newDto) {
        Map<String, Object> changes = new HashMap<>();

        putIfChanged(changes, "description",
            getFieldValue(oldDto, "description"),
            getFieldValue(newDto, "description")
        );
        putIfChanged(changes, "type",
            safeGet(oldDto.getType(), InvoiceChargeType::getName),
            safeGet(newDto.getType(), InvoiceChargeType::getName)
        );
        putIfChangedBigDecimal(changes, "value", oldDto.getValue(), newDto.getValue());

        return changes;
    }

    private Map<String, Object> getValidateChangesTax(InvoiceTaxDTO oldDto, InvoiceTaxDTO newDto) {
        Map<String, Object> changes = new HashMap<>();

        putIfChanged(changes, "description",
            getFieldValue(oldDto, "description"),
            getFieldValue(newDto, "description")
        );
        putIfChanged(changes, "type",
            safeGet(oldDto.getType(), InvoiceTaxType::getName),
            safeGet(newDto.getType(), InvoiceTaxType::getName)
        );
        putIfChangedBigDecimal(changes, "rate", oldDto.getRate(), newDto.getRate());
        putIfChangedBigDecimal(changes, "taxableBase", oldDto.getTaxableBase(), newDto.getTaxableBase());
        putIfChangedBigDecimal(changes, "value", oldDto.getValue(), newDto.getValue());

        return changes;
    }

    private Map<String, Object> getValidateChangesPayment(InvoicePaymentDTO oldDto, InvoicePaymentDTO newDto) {
        Map<String, Object> changes = new HashMap<>();

        putIfChanged(changes, "voidedReason",
            getFieldValue(oldDto, "voidedReason"),
            getFieldValue(newDto, "voidedReason")
        );
        if (oldDto.isVoided() != newDto.isVoided()) {
            changes.put("voided", getChangeItem(oldDto.isVoided(), newDto.isVoided()));
        }
        putIfChanged(changes, "voidedAt",
            getFieldValue(oldDto, "voidedAt"),
            getFieldValue(newDto, "voidedAt")
        );
        compareOther(changes, "voidedBy",
            safeGet(oldDto.getVoidedBy(), UserDTO::getId),
            safeGet(newDto.getVoidedBy(), UserDTO::getId),
            safeGet(oldDto.getVoidedBy(), UserDTO::getFullName),
            safeGet(newDto.getVoidedBy(), UserDTO::getFullName)
        );

        return changes;
    }
}
