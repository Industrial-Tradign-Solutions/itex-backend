package com.itradingsolutions.itex.api.common.util.repositories;

import com.itradingsolutions.itex.api.common.util.models.entities.HistoryEntity;
import com.itradingsolutions.itex.api.common.util.models.enums.HistoryActions;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.ZonedDateTime;
import java.util.UUID;

public interface IHistoryRepository extends CrudRepository<HistoryEntity, UUID> {

    @Modifying
    @Query("DELETE FROM HistoryEntity h WHERE h.createdAt < :createdAt AND h.action = :action")
    void fetchDeleteHistoryByActionsAndDate(ZonedDateTime createdAt, HistoryActions action);

    @Modifying
    @Query("DELETE FROM HistoryEntity h WHERE h.createdAt < :createdAt AND h.basic = true")
    void fetchDeleteHistoryDate(ZonedDateTime createdAt);

}
