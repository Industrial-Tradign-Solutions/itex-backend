package com.itradingsolutions.itex.api.ip.q.service.impl;

import com.itradingsolutions.itex.api.common.service.impl.HistoryServiceImpl;
import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationDTO;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationHistoryDTO;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationOtherChargeDTO;
import com.itradingsolutions.itex.api.ip.q.models.dto.IpQuotationProductDTO;
import com.itradingsolutions.itex.api.ip.q.models.entities.IpQuotationHistoryEntity;
import com.itradingsolutions.itex.api.ip.q.models.enums.IpQuotationHistoryAction;
import com.itradingsolutions.itex.api.ip.q.models.mapper.IpQuotationHistoryMapper;
import com.itradingsolutions.itex.api.ip.q.repository.IIpQuotationHistoryRepository;
import com.itradingsolutions.itex.api.ip.q.service.IIpQuotationHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service implementation for managing IP Quotation history.
 */
@Service
@RequiredArgsConstructor
public class IpQuotationHistoryServiceImpl extends HistoryServiceImpl implements IIpQuotationHistoryService {

    private final IIpQuotationHistoryRepository repository;
    private final IpQuotationHistoryMapper mapper;

    @Override
    @Transactional
    public void addHistory(IpQuotationHistoryAction action, IpQuotationDTO oldDto, IpQuotationDTO newDto) {
        validateNotNull(newDto, "Data is not null");
        var entity = new IpQuotationHistoryEntity();
        entity.setIpQuotation(newDto.getId());
        entity.setData(resolveHistoryData(action, oldDto, newDto));
        addHistoryCommon(action, entity);
    }

    @Override
    @Transactional
    public void addHistoryProduct(IpQuotationHistoryAction action, IpQuotationProductDTO oldDto, IpQuotationProductDTO newDto, UUID quotationId) {
        validateNotNull(newDto != null || oldDto != null ? (newDto != null ? newDto : oldDto) : null, "Data is not null");
        var entity = new IpQuotationHistoryEntity();
        entity.setIpQuotation(quotationId);
        entity.setData(resolveHistoryProductData(action, oldDto, newDto));
        addHistoryCommon(action, entity);
    }

    @Override
    @Transactional
    public void addHistoryOtherCharge(IpQuotationHistoryAction action, IpQuotationOtherChargeDTO oldDto, IpQuotationOtherChargeDTO newDto, UUID quotationId) {
        validateNotNull(newDto != null || oldDto != null ? (newDto != null ? newDto : oldDto) : null, "Data is not null");
        var entity = new IpQuotationHistoryEntity();
        entity.setIpQuotation(quotationId);
        entity.setData(resolveHistoryOtherChargeData(action, oldDto, newDto));
        addHistoryCommon(action, entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IpQuotationHistoryDTO> getHistoryById(UUID quotationId) {
        var list = repository.fetchByIpQuotationId(quotationId);
        return list.stream().map(mapper::entityToDTO).toList();
    }

    private void addHistoryCommon(IpQuotationHistoryAction action, IpQuotationHistoryEntity entity) {
        entity.setAction(action);
        entity.setUser(getUserAuthUser());
        // For UPDATE actions, only save if there are actual changes
        if (action == IpQuotationHistoryAction.UPDATE || action == IpQuotationHistoryAction.UPDATE_PRODUCT || action == IpQuotationHistoryAction.UPDATE_OTHER_CHARGE) {
            if (entity.getData() != null && !entity.getData().isEmpty()) {
                repository.save(entity);
            }
        } else {
            repository.save(entity);
        }
    }

    private Map<String, Object> resolveHistoryData(IpQuotationHistoryAction action, IpQuotationDTO oldDto, IpQuotationDTO newDto) {
        return switch (action) {
            case CREATE -> createData(newDto);
            case UPDATE -> updateData(oldDto, newDto);
            case CLONE -> cloneData(newDto);
            case REJECTED -> rejectedData(newDto);
            case STATUS_CHANGE -> statusChangeData(oldDto, newDto);
            case ADD_QR -> addQrData(newDto);
            case REMOVE_QR -> removeQrData(newDto);
            default -> throw new BadRequestException("Action not supported: " + action);
        };
    }

    private Map<String, Object> resolveHistoryProductData(IpQuotationHistoryAction action, IpQuotationProductDTO oldDto, IpQuotationProductDTO newDto) {
        return switch (action) {
            case ADD_PRODUCT -> addProductData(newDto);
            case UPDATE_PRODUCT -> updateProductData(oldDto, newDto);
            case REMOVE_PRODUCT -> removeProductData(oldDto);
            default -> throw new BadRequestException("Action not supported for products: " + action);
        };
    }

    private Map<String, Object> resolveHistoryOtherChargeData(IpQuotationHistoryAction action, IpQuotationOtherChargeDTO oldDto, IpQuotationOtherChargeDTO newDto) {
        return switch (action) {
            case ADD_OTHER_CHARGE -> addOtherChargeData(newDto);
            case UPDATE_OTHER_CHARGE -> updateOtherChargeData(oldDto, newDto);
            case REMOVE_OTHER_CHARGE -> removeOtherChargeData(newDto);
            default -> throw new BadRequestException("Action not supported for other charges: " + action);
        };
    }

    private Map<String, Object> createData(IpQuotationDTO dto) {
        Map<String, Object> data = new HashMap<>();
        data.put("number", dto.getNumber());
        data.put("status", dto.getStatus().name());
        data.put("currency", dto.getCurrency().name());
        compareOther(data, "client", null,
                dto.getClient() != null ? dto.getClient().getId() : null,
                null,
                dto.getClient() != null ? dto.getClient().getName() : null);
        compareOther(data, "clientContact", null, 
                dto.getClientContact() != null ? dto.getClientContact().getId() : null,
                null,
                dto.getClientContact() != null ? dto.getClientContact().getName() : null);
        putIfChanged(data, "clientQNumber", null, dto.getClientQNumber());
        compareOther(data, "salesRep", null,
                dto.getSalesRep() != null ? dto.getSalesRep().getId() : null,
                null,
                dto.getSalesRep() != null ? dto.getSalesRep().getFullName() : null);
        return data;
    }

    private Map<String, Object> updateData(IpQuotationDTO oldDto, IpQuotationDTO newDto) {
        Map<String, Object> data = new HashMap<>();
        compareOther(data, "salesRep",
                oldDto.getSalesRep() != null ? oldDto.getSalesRep().getId() : null,
                newDto.getSalesRep() != null ? newDto.getSalesRep().getId() : null,
                oldDto.getSalesRep() != null ? oldDto.getSalesRep().getFullName() : null,
                newDto.getSalesRep() != null ? newDto.getSalesRep().getFullName() : null);
        compareOther(data, "clientContact",
                oldDto.getClientContact() != null ? oldDto.getClientContact().getId() : null,
                newDto.getClientContact() != null ? newDto.getClientContact().getId() : null,
                oldDto.getClientContact() != null ? oldDto.getClientContact().getName() : null,
                newDto.getClientContact() != null ? newDto.getClientContact().getName() : null);
        putIfChanged(data, "clientQNumber", oldDto.getClientQNumber(), newDto.getClientQNumber());
        putIfChanged(data, "remarks", oldDto.getRemarks(), newDto.getRemarks());
        putIfChanged(data, "internalRemarks", oldDto.getInternalRemarks(), newDto.getInternalRemarks());
        putIfChanged(data, "leadTime", oldDto.getLeadTime(), newDto.getLeadTime());
        compareEnum(data, "leadTimeType", oldDto.getLeadTimeType(), newDto.getLeadTimeType());
        putIfChanged(data, "validity", oldDto.getValidity(), newDto.getValidity());
        compareEnum(data, "validityType", oldDto.getValidityType(), newDto.getValidityType());
        putIfChanged(data, "incoterms", oldDto.getIncoterms(), newDto.getIncoterms());
        putIfChanged(data, "paymentTerms", oldDto.getPaymentTerms(), newDto.getPaymentTerms());
        return data;
    }

    private Map<String, Object> cloneData(IpQuotationDTO dto) {
        Map<String, Object> data = new HashMap<>();
        data.put("clonedNumber", dto.getNumber());
        return data;
    }

    private Map<String, Object> rejectedData(IpQuotationDTO dto) {
        Map<String, Object> data = new HashMap<>();
        data.put("number", dto.getNumber());
        data.put("status", "REJECTED");
        return data;
    }

    private Map<String, Object> statusChangeData(IpQuotationDTO oldDto, IpQuotationDTO newDto) {
        Map<String, Object> data = new HashMap<>();
        data.put("oldStatus", oldDto.getStatus().name());
        data.put("newStatus", newDto.getStatus().name());
        return data;
    }

    private Map<String, Object> addQrData(IpQuotationDTO dto) {
        Map<String, Object> data = new HashMap<>();
        data.put("quoteRequestsCount", dto.getListQuoteRequests() != null ? dto.getListQuoteRequests().size() : 0);
        return data;
    }

    private Map<String, Object> removeQrData(IpQuotationDTO dto) {
        Map<String, Object> data = new HashMap<>();
        data.put("quoteRequestsCount", dto.getListQuoteRequests() != null ? dto.getListQuoteRequests().size() : 0);
        return data;
    }

    private Map<String, Object> addProductData(IpQuotationProductDTO dto) {
        Map<String, Object> data = new HashMap<>();
        data.put("lineNumber", dto.getNumber());
        if (dto.getQuoteRequestProduct() != null && dto.getQuoteRequestProduct().getIpProduct() != null) {
            data.put("productReference", dto.getQuoteRequestProduct().getIpProduct().getMfrReference());
            data.put("productDescription", dto.getQuoteRequestProduct().getIpProduct().getDescription());
        }
        putIfChangedBigDecimal(data, "profitMargin", null, dto.getProfitMargin());
        data.put("condition", dto.getCondition().name());
        return data;
    }

    private Map<String, Object> updateProductData(IpQuotationProductDTO oldDto, IpQuotationProductDTO newDto) {
        Map<String, Object> data = new HashMap<>();
        data.put("lineNumber", newDto.getNumber());
        putIfChangedBigDecimal(data, "profitMargin", oldDto.getProfitMargin(), newDto.getProfitMargin());
        compareEnum(data, "condition", oldDto.getCondition(), newDto.getCondition());
        return data;
    }

    private Map<String, Object> removeProductData(IpQuotationProductDTO dto) {
        Map<String, Object> data = new HashMap<>();
        data.put("lineNumber", dto.getNumber());
        if (dto.getQuoteRequestProduct() != null && dto.getQuoteRequestProduct().getIpProduct() != null) {
            data.put("productReference", dto.getQuoteRequestProduct().getIpProduct().getMfrReference());
        }
        return data;
    }

    private void compareEnum(Map<String, Object> data, String field, Enum<?> oldValue, Enum<?> newValue) {
        if (oldValue != newValue) {
            Map<String, String> change = new HashMap<>();
            change.put("old", oldValue != null ? oldValue.name() : null);
            change.put("new", newValue != null ? newValue.name() : null);
            data.put(field, change);
        }
    }

    /**
     * Creates history data for adding an other charge.
     */
    private Map<String, Object> addOtherChargeData(IpQuotationOtherChargeDTO dto) {
        Map<String, Object> data = new HashMap<>();
        data.put("description", dto.getDescription());
        putIfChangedBigDecimal(data, "value", null, dto.getValue());
        return data;
    }

    /**
     * Creates history data for updating an other charge.
     */
    private Map<String, Object> updateOtherChargeData(IpQuotationOtherChargeDTO oldDto, IpQuotationOtherChargeDTO newDto) {
        Map<String, Object> data = new HashMap<>();
        putIfChanged(data, "description", oldDto.getDescription(), newDto.getDescription());
        putIfChangedBigDecimal(data, "value", oldDto.getValue(), newDto.getValue());
        return data;
    }

    /**
     * Creates history data for removing an other charge.
     */
    private Map<String, Object> removeOtherChargeData(IpQuotationOtherChargeDTO dto) {
        Map<String, Object> data = new HashMap<>();
        data.put("description", dto.getDescription());
        data.put("value", dto.getValue());
        return data;
    }
}
