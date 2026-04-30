package com.itradingsolutions.itex.api.common.util.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleOption;
import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;
import com.itradingsolutions.itex.api.common.util.models.entities.HistoryEntity;
import com.itradingsolutions.itex.api.common.util.models.enums.HistoryActions;
import com.itradingsolutions.itex.api.common.util.repositories.IHistoryRepository;
import com.itradingsolutions.itex.api.common.util.services.IHistoryService;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl extends UtilServiceAbs implements IHistoryService {

    private final IHistoryRepository historyRepository;
    private final IUserService userService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void saveHistory(HistoryActions action, ModuleOption module, UUID modifiedRecord, Object oldData, Object newData) {
        HistoryEntity historyEntity = new HistoryEntity();
        historyEntity.setAction(action);
        historyEntity.setModifiedRecord(modifiedRecord);
        historyEntity.setModule(module.getId());
        historyEntity.setUserExecutedAction(userService.getUserAuthenticated());
        historyEntity.setOldData(convertObjectToMap(oldData));
        historyEntity.setNewData(convertObjectToMap(newData));
        historyEntity.setBasic(true);
        historyRepository.save(historyEntity);
    }

    @Override
    @Transactional
    public void saveHistoryNotData(HistoryActions action, String username) {
        HistoryEntity historyEntity = new HistoryEntity();
        historyEntity.setAction(action);
        historyEntity.setUserExecutedAction(userService.getUserByUsername(username));
        historyEntity.setModifiedRecord(historyEntity.getUserExecutedAction().getId());
        historyEntity.setModule(ModuleOption.NOT_MODULE.getId());
        historyRepository.save(historyEntity);
    }

    @Override
    @Transactional
    public void deleteHistory(HistoryActions action) {
        ZonedDateTime threeMonthsAgo = ZonedDateTime.now(zoneId).minusMonths(3);
        historyRepository.fetchDeleteHistoryByActionsAndDate(threeMonthsAgo, action);
    }

    @Override
    @Transactional
    public void deleteBasicHistory() {
        ZonedDateTime threeMonthsAgo = ZonedDateTime.now(zoneId).minusYears(1);
        historyRepository.fetchDeleteHistoryDate(threeMonthsAgo);
    }

    @Override
    @Transactional
    public <T extends BaseDTO> void importList(ModuleOption module, List<T> newData) {
        List<HistoryEntity> entities = new ArrayList<>();
        var user = userService.getUserAuthenticated();
        for (T object : newData) {
            HistoryEntity entity = new HistoryEntity();
            entity.setAction(HistoryActions.IMPORT_FILE);
            entity.setModule(module.getId());
            entity.setModifiedRecord(object.getId());
            entity.setUserExecutedAction(user);
            entity.setOldData(null);
            entity.setNewData(convertObjectToMap(object));
            entity.setBasic(true);
            entities.add(entity);
        }
        historyRepository.saveAll(entities);
    }




    @SuppressWarnings("unchecked")
    private Map<String, Object> convertObjectToMap(Object object) {
        if (object == null)
            return null;
        return objectMapper.convertValue(object, HashMap.class);
    }

}

