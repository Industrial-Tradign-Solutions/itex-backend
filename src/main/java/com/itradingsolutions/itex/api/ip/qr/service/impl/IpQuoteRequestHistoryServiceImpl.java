package com.itradingsolutions.itex.api.ip.qr.service.impl;

import com.itradingsolutions.itex.api.admin.user.models.dto.UserDTO;
import com.itradingsolutions.itex.api.common.models.enums.LeadTime;
import com.itradingsolutions.itex.api.common.service.impl.HistoryServiceImpl;
import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;
import com.itradingsolutions.itex.api.common.util.models.enums.Currency;
import com.itradingsolutions.itex.api.common.util.models.enums.FreightClass;
import com.itradingsolutions.itex.api.common.util.models.enums.PaymentTerms;
import com.itradingsolutions.itex.api.common.util.models.enums.UnitType;
import com.itradingsolutions.itex.api.ip.products.models.dto.IpProductDTO;
import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestDTO;
import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestHistoryDTO;
import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestOtherChargesDTO;
import com.itradingsolutions.itex.api.ip.qr.models.dto.IpQuoteRequestProductDTO;
import com.itradingsolutions.itex.api.ip.qr.models.entities.IpQuoteRequestHistoryEntity;
import com.itradingsolutions.itex.api.ip.qr.models.enums.IpQuoteRequestHistoryAction;
import com.itradingsolutions.itex.api.ip.qr.models.enums.IpQuoteRequestStatus;
import com.itradingsolutions.itex.api.ip.qr.models.mappers.IpQuoteRequestHistoryMapper;
import com.itradingsolutions.itex.api.ip.qr.repositories.IIpQuoteRequestHistoryRepository;
import com.itradingsolutions.itex.api.ip.qr.service.IIpQuoteRequestHistoryService;
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
public class IpQuoteRequestHistoryServiceImpl extends HistoryServiceImpl implements IIpQuoteRequestHistoryService {

    private final IIpQuoteRequestHistoryRepository repository;
    private final IpQuoteRequestHistoryMapper mapper;

    @Override
    @Transactional
    public void addHistory(IpQuoteRequestHistoryAction action, IpQuoteRequestDTO oldDto, IpQuoteRequestDTO newDto) {
        validateNotNull(newDto, "Data is not null");
        var entity = new IpQuoteRequestHistoryEntity();
        entity.setIpQuoteRequest(newDto.getId());
        entity.setData(resolveHistoryData(action, oldDto, newDto));
        addHistoryCommon(action, entity);
    }

    @Override
    @Transactional
    public void addHistoryProduct(IpQuoteRequestHistoryAction action, IpQuoteRequestProductDTO oldDto, IpQuoteRequestProductDTO newDto, UUID qrId) {
        validateNotNull(newDto, "Data is not null");
        var entity = new IpQuoteRequestHistoryEntity();
        entity.setIpQuoteRequest(qrId);
        entity.setData(resolveHistoryProductData(action, oldDto, newDto));
        addHistoryCommon(action, entity);
    }

    @Override
    @Transactional
    public void addHistoryOtherCharge(IpQuoteRequestHistoryAction action, IpQuoteRequestOtherChargesDTO oldDto, IpQuoteRequestOtherChargesDTO newDto, UUID qrId) {
        validateNotNull(newDto, "Data is not null");
        var entity = new IpQuoteRequestHistoryEntity();
        entity.setIpQuoteRequest(qrId);
        entity.setData(resolveHistoryOtherChargeData(action, oldDto, newDto));
        addHistoryCommon(action, entity);
    }


    @Override
    @Transactional(readOnly = true)
    public List<IpQuoteRequestHistoryDTO> getHistoryById(UUID id) {
        var list = repository.fetchByIpQrId(id);
        return list.stream().map(mapper::entityToDTO).toList();
    }

    private void addHistoryCommon(IpQuoteRequestHistoryAction action, IpQuoteRequestHistoryEntity entity) {
        entity.setUser(getUserAuthUser());
        entity.setAction(action);
        if (action.equals(IpQuoteRequestHistoryAction.UPDATE) || action.equals(IpQuoteRequestHistoryAction.UPDATE_PRODUCT)) {
            if (!entity.getData().isEmpty())
                repository.save(entity);
        } else {
            repository.save(entity);
        }
    }

    private Map<String, Object> resolveHistoryProductData(IpQuoteRequestHistoryAction action, IpQuoteRequestProductDTO oldDto, IpQuoteRequestProductDTO newDto) {
        return switch (action) {
            case ADD_PRODUCT, REMOVE_PRODUCT -> convertToMap(newDto, true, true);
            case UPDATE_PRODUCT -> {
                validateNotNull(oldDto, "oldDto must not be null for UPDATE");
                yield getValidateChangesProduct(oldDto, newDto);
            }
            default -> throw new BadRequestException("Invalid action");
        };
    }

    private Map<String, Object> resolveHistoryOtherChargeData(IpQuoteRequestHistoryAction action, IpQuoteRequestOtherChargesDTO oldDto, IpQuoteRequestOtherChargesDTO newDto) {
        return switch (action) {
            case ADD_OTHER_CHARGE, REMOVE_OTHER_CHARGE -> convertToMap(newDto, true, true);
            case UPDATE_OTHER_CHARGE -> {
                validateNotNull(oldDto, "oldDto must not be null for UPDATE");
                yield getValidateChangesOtherCharges(oldDto, newDto);
            }
            default -> throw new BadRequestException("Invalid action");
        };
    }

    private Map<String, Object> resolveHistoryData(IpQuoteRequestHistoryAction action, IpQuoteRequestDTO oldDto, IpQuoteRequestDTO newDto) {
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
            case QUOTED -> {
                validateNotNull(oldDto, "oldDto must not be null for Quoted");
                yield getValidateChangesQuoted(oldDto);
            }
            default -> throw new BadRequestException("Invalid action");
        };
    }



    private Map<String, Object> getValidateChanges(IpQuoteRequestDTO oldDto, IpQuoteRequestDTO newDto) {
        Map<String, Object> changes = new HashMap<>();

        // 🔹 Campos simples
        List<String> simpleFields = Arrays.asList(
                "clientQrNumber", "internalRemarks",
                "supplierQrNumber", "number",
                "remarks", "shippingPointZipCode", "fobShippingPoint",
                "sentAt", "answeredAt", "completeAt","rejectAt"
        );

        simpleFields.forEach(field ->
                putIfChanged(changes, field, getFieldValue(oldDto, field), getFieldValue(newDto, field))
        );


        // 🔹 BigDecimal
        putIfChangedBigDecimal(changes, "grossWeightLbs", oldDto.getGrossWeightLbs(), newDto.getGrossWeightLbs());
        putIfChangedBigDecimal(changes, "freightCharges", oldDto.getFreightCharges(), newDto.getFreightCharges());
        putIfChangedBigDecimal(changes, "totalOtherCharges", oldDto.getTotalOtherCharges(), newDto.getTotalOtherCharges());
        putIfChangedBigDecimal(changes, "total", oldDto.getTotal(), newDto.getTotal());
        putIfChangedBigDecimal(changes, "subTotal", oldDto.getSubTotal(), newDto.getSubTotal());

        // Otros
        compareOther(
                changes,
                "client",
                safeGet(oldDto.getClient(), ClientDTO::getId),
                safeGet(newDto.getClient(), ClientDTO::getId),
                safeGet(oldDto.getClient(), ClientDTO::getCode),
                safeGet(newDto.getClient(), ClientDTO::getCode)
        );
        compareOther(
                changes,
                "clientContact",
                safeGet(oldDto.getClientContact(), ClientContactDTO::getId),
                safeGet(newDto.getClientContact(), ClientContactDTO::getId),
                safeGet(oldDto.getClientContact(), ClientContactDTO::getName),
                safeGet(newDto.getClientContact(), ClientContactDTO::getName)
        );
        compareOther(
                changes,
                "salesRep",
                safeGet(oldDto.getSalesRep(), UserDTO::getId),
                safeGet(newDto.getSalesRep(), UserDTO::getId),
                safeGet(oldDto.getSalesRep(), UserDTO::getFullName),
                safeGet(newDto.getSalesRep(), UserDTO::getFullName)
        );
        compareOther(
                changes,
                "supplier",
                safeGet(oldDto.getSupplier(), SupplierDTO::getId),
                safeGet(newDto.getSupplier(), SupplierDTO::getId),
                safeGet(oldDto.getSupplier(), SupplierDTO::getName),
                safeGet(newDto.getSupplier(), SupplierDTO::getName)
        );
        compareOther(
                changes,
                "supplierContact",
                safeGet(oldDto.getSupplierContact(), SupplierContactDTO::getId),
                safeGet(newDto.getSupplierContact(), SupplierContactDTO::getId),
                safeGet(oldDto.getSupplierContact(), SupplierContactDTO::getName),
                safeGet(newDto.getSupplierContact(), SupplierContactDTO::getName)
        );

        // 🔹 Enums
        putIfChanged(changes, "status",
                safeGet(oldDto.getStatus(), IpQuoteRequestStatus::getName),
                safeGet(newDto.getStatus(), IpQuoteRequestStatus::getName)
        );
        putIfChanged(changes, "currency",
                safeGet(oldDto.getCurrency(), Currency::getName),
                safeGet(newDto.getCurrency(), Currency::getName)
        );

        putIfChanged(changes, "freightClass",
                safeGet(oldDto.getFreightClass(), FreightClass::getType),
                safeGet(newDto.getFreightClass(), FreightClass::getType)
        );

        putIfChanged(changes, "paymentTerms",
                safeGet(oldDto.getPaymentTerms(), PaymentTerms::getName),
                safeGet(newDto.getPaymentTerms(), PaymentTerms::getName)
        );

        return changes;
    }

    private Map<String, Object> getValidateChangesProduct(IpQuoteRequestProductDTO oldDto, IpQuoteRequestProductDTO newDto) {
        Map<String, Object> changes = new HashMap<>();
        changes.put("qrProdId", oldDto.getId());

        // 🔹 Campos simples
        List<String> simpleFields = Arrays.asList(
                "number", "leadTime"
        );

        simpleFields.forEach(field ->
                putIfChanged(changes, field, getFieldValue(oldDto, field), getFieldValue(newDto, field))
        );
        // 🔹 BigDecimal
        putIfChangedBigDecimal(changes, "quantity", oldDto.getQuantity(), newDto.getQuantity());
        putIfChangedBigDecimal(changes, "unitPrice", oldDto.getUnitPrice(), newDto.getUnitPrice());
        putIfChangedBigDecimal(changes, "extendedPrice", oldDto.getExtendedPrice(), newDto.getExtendedPrice());
        putIfChangedBigDecimal(changes, "grossWeightLbs", oldDto.getGrossWeightLbs(), newDto.getGrossWeightLbs());

        // 🔹 Enums
        putIfChanged(changes, "unitType",
                safeGet(oldDto.getUnitType(), UnitType::getName),
                safeGet(newDto.getUnitType(), UnitType::getName)
        );

        putIfChanged(changes, "leadTimeType",
                safeGet(oldDto.getLeadTimeType(), LeadTime::getName),
                safeGet(newDto.getLeadTimeType(), LeadTime::getName)
        );

        // 🔹 Others
        compareOther(
                changes,
                "product",
                safeGet(oldDto.getIpProduct(), IpProductDTO::getId),
                safeGet(newDto.getIpProduct(), IpProductDTO::getId),
                safeGet(oldDto.getIpProduct(), IpProductDTO::getDescription),
                safeGet(newDto.getIpProduct(), IpProductDTO::getDescription)
        );
        return changes;
    }

    private Map<String, Object> getValidateChangesOtherCharges(IpQuoteRequestOtherChargesDTO oldDto, IpQuoteRequestOtherChargesDTO newDto) {
        Map<String, Object> changes = new HashMap<>();
        changes.put("qrOtherChargesId", oldDto.getId());

        // 🔹 Campos simples
        List<String> simpleFields = List.of(
                "description"
        );

        simpleFields.forEach(field ->
                putIfChanged(changes, field, getFieldValue(oldDto, field), getFieldValue(newDto, field))
        );
        // 🔹 BigDecimal
        putIfChangedBigDecimal(changes, "value", oldDto.getValue(), newDto.getValue());
        return changes;
    }

    private Map<String, Object> getValidateChangesClone(IpQuoteRequestDTO oldDto) {
        Map<String, Object> changes = convertToMap(oldDto, true, true);
        changes.put("clonedBy", oldDto.getNumber());
        changes.remove("clonedByQr");
        changes.remove("clonedQrs");
        return changes;
    }

    private Map<String, Object> getValidateChangesQuoted(IpQuoteRequestDTO oldDto) {
        throw  new UnsupportedOperationException("implementar funcion para cargar el codigo de la Cotizacion");
        //Map<String, Object> changes = new HashMap<>();
        //changes.put("quoted", oldDto.getNumber());
        //return changes;
    }
}
