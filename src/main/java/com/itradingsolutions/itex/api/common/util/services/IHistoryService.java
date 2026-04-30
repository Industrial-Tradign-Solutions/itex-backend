package com.itradingsolutions.itex.api.common.util.services;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleOption;
import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;
import com.itradingsolutions.itex.api.common.util.models.enums.HistoryActions;

import java.util.List;
import java.util.UUID;

public interface IHistoryService {

    void saveHistory(HistoryActions action, ModuleOption module, UUID modifiedRecord, Object oldData, Object newData);
    void saveHistoryNotData(HistoryActions action, String username);
    void deleteHistory(HistoryActions action);
    void deleteBasicHistory();
    <T extends BaseDTO>void importList(ModuleOption module, List<T> newData);
}
