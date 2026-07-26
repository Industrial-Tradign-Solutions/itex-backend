package com.itradingsolutions.itex.api.common.consecutive.services.impl;

import com.itradingsolutions.itex.api.common.consecutive.models.entities.ConsecutiveEntity;
import com.itradingsolutions.itex.api.common.consecutive.models.entities.ConsecutiveId;
import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveDepartment;
import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveModule;
import com.itradingsolutions.itex.api.common.consecutive.repositories.IConsecutiveRepository;
import com.itradingsolutions.itex.api.common.consecutive.services.IConsecutiveService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ConsecutiveServiceImpl implements IConsecutiveService {

    private final IConsecutiveRepository consecutiveRepository;

    @Override
    @Transactional
    public void saveConsecutive(ConsecutiveModule module, ConsecutiveDepartment department, String consecutive) {
        consecutiveRepository.save(new ConsecutiveEntity(module, department, consecutive));
    }

    @Override
    @Transactional
    public void deleteConsecutive(ConsecutiveModule module, ConsecutiveDepartment department, String consecutive) {
        consecutiveRepository.delete(new ConsecutiveEntity(module, department, consecutive));
    }

    @Override
    @Transactional(readOnly = true)
    public String generateConsecutive(ConsecutiveModule module, ConsecutiveDepartment department, String clientCode) {
        LocalDateTime now = LocalDateTime.now();
        String year = String.valueOf(now.getYear()).substring(2);
        String month = String.format("%02d", now.getMonthValue());

        ConsecutiveEntity consecutive = getConsecutiveByPrefix(clientCode, year, month, module, department);
        return consecutive.getConsecutive();
    }

    private ConsecutiveEntity getConsecutiveByPrefix(String clientCode, String year, String month, ConsecutiveModule module, ConsecutiveDepartment department) {
        var consecutive = consecutiveRepository.fetchMaxByPrefix(clientCode, year, month, module, department).orElse(0) ;
        return new ConsecutiveEntity(
                new ConsecutiveId(department, clientCode, year, month, consecutive + 1, module)
        );
    }
}
