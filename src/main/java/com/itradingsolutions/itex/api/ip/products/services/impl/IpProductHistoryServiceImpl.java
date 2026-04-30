package com.itradingsolutions.itex.api.ip.products.services.impl;

import com.itradingsolutions.itex.api.common.service.impl.HistoryServiceImpl;
import com.itradingsolutions.itex.api.common.util.models.enums.FreightClass;
import com.itradingsolutions.itex.api.ip.products.models.dto.IpProductDTO;
import com.itradingsolutions.itex.api.ip.products.models.dto.IpProductHistoryDTO;
import com.itradingsolutions.itex.api.ip.products.models.entity.IpProductHistoryEntity;
import com.itradingsolutions.itex.api.ip.products.models.enums.IpProductHistoryActions;
import com.itradingsolutions.itex.api.ip.products.models.mappers.IpProductHistoryMapper;
import com.itradingsolutions.itex.api.ip.products.repositories.IIpProductHistoryRepository;
import com.itradingsolutions.itex.api.ip.products.services.IIpProductHistoryService;

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
public class IpProductHistoryServiceImpl extends HistoryServiceImpl implements IIpProductHistoryService {

    private final IIpProductHistoryRepository repository;
    private final IpProductHistoryMapper mapper;

    @Override
    @Transactional
    public void addHistory(IpProductHistoryActions action, IpProductDTO oldDto, IpProductDTO newDto) {
        validateNotNull(newDto, "Data is not null");
        var entity = new IpProductHistoryEntity();
        entity.setUser(getUserAuthUser());
        entity.setAction(action);
        entity.setData(resolveHistoryData(action, oldDto, newDto));
        entity.setProduct(action.equals(IpProductHistoryActions.REPLACE) ? oldDto.getId() : newDto.getId());

        if (action.equals(IpProductHistoryActions.UPDATE)) {
            if (!entity.getData().isEmpty())
                repository.save(entity);
        } else {
            repository.save(entity);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<IpProductHistoryDTO> getHistoryById(UUID id) {
        var list = repository.fetchByProductId(id);
        return list.stream().map(mapper::entityToDTO).toList();
    }

    private Map<String, Object> resolveHistoryData(IpProductHistoryActions action, IpProductDTO oldDto, IpProductDTO newDto) {
        return switch (action) {
            case CREATE, IMPORT_PRODUCT -> convertToMap(newDto, true, true);
            case UPDATE -> {
                validateNotNull(oldDto, "oldDto must not be null for UPDATE");
                yield getValidateChanges(oldDto, newDto);
            }
            case REPLACE -> {
                validateNotNull(oldDto, "oldDto must not be null for REPLACE");
                yield getValidateChangesReplace(oldDto, newDto);
            }
            case ADD_SURPLUS, REMOVE_SURPLUS -> {
                validateNotNull(oldDto, "oldDto must not be null for SURPLUS actions");
                yield getSurplusData(oldDto, newDto);
            }
            default -> null;
        };
    }

    private Map<String, Object> getSurplusData(IpProductDTO oldDto, IpProductDTO newDto) {
        Map<String, Object> changes = new HashMap<>();
        changes.put("old", oldDto.getTotalSurplus());
        changes.put("new", newDto.getTotalSurplus());
        return  changes;
    }

    private Map<String, Object> getValidateChangesReplace(IpProductDTO oldDto, IpProductDTO newDto) {
        Map<String, Object> changes = new HashMap<>();
        // Campos simples
        putIfChanged(changes, "id", oldDto.getId(), newDto.getId());
        putIfChanged(changes, "description", oldDto.getDescription(), newDto.getDescription());
        return changes;
    }

    private Map<String, Object> getValidateChanges(IpProductDTO oldDto, IpProductDTO newDto) {
        Map<String, Object> changes = new HashMap<>();

        // 🔹 Campos simples
        List<String> simpleFields = Arrays.asList(
                "description", "clientDescription", "mfrReference",
                "clientReference", "nmfc", "notes",
                "keywords", "htsScheduleBNumber", "eccn"
        );

        simpleFields.forEach(field ->
                putIfChanged(changes, field, getFieldValue(oldDto, field), getFieldValue(newDto, field))
        );

        //Big Decimal
        putIfChangedBigDecimal(changes, "netWeightLbs", oldDto.getNetWeightLbs(), newDto.getNetWeightLbs());

        // Campos booleanos
        if (oldDto.isBattery() != newDto.isBattery())
            changes.put("battery", getChangeItem(oldDto.isBattery(), newDto.isBattery()));

        if (oldDto.isHazmat() != newDto.isHazmat())
            changes.put("hazmat", getChangeItem(oldDto.isHazmat(), newDto.isHazmat()));

        if (oldDto.isDualUse() != newDto.isDualUse())
            changes.put("dualUse", getChangeItem(oldDto.isDualUse(), newDto.isDualUse()));


        // Campos anidados
        compareMaster(changes, "brand", oldDto.getBrand(), newDto.getBrand());
        compareMaster(changes, "coo", oldDto.getCoo(), newDto.getCoo());

        // FreightClass puede ser nulo, acceder a su tipo con validación
        putIfChanged(changes, "freightClass",
                safeGet(oldDto.getFreightClass(), FreightClass::getType),
                safeGet(newDto.getFreightClass(), FreightClass::getType)
        );

        return changes;
    }
}
