package com.itradingsolutions.itex.api.common.util.schedulers;

import com.itradingsolutions.itex.api.common.util.models.enums.HistoryActions;
import com.itradingsolutions.itex.api.common.util.services.IHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class HistoryScheduler {

    private final IHistoryService historyService;

    @Scheduled(cron = "0 30 13 * * SUN")
    private void deleteHistory() {
        historyService.deleteHistory(HistoryActions.LOGIN);
        historyService.deleteHistory(HistoryActions.LOGOUT);
        historyService.deleteHistory(HistoryActions.CHANGE_PASSWORD);
        historyService.deleteHistory(HistoryActions.LOCK);
        historyService.deleteHistory(HistoryActions.UNLOCK);
    }
}
