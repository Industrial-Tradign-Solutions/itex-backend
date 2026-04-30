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
import java.util.List;

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
        if (module == ConsecutiveModule.INV) throw new RuntimeException("Consecutive module INV is not supported");

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

    private ConsecutiveEntity getConsecutiveByPrefixBinary(String clientCode, String year, String month, ConsecutiveModule module, ConsecutiveDepartment department) {
        var list = consecutiveRepository.fetchByPrefix(clientCode, year, month, module, department);
        var consecutive = findFirstMissingNumberBinary(list);
        return new ConsecutiveEntity(
                        new ConsecutiveId(department, clientCode, year, month, consecutive, module)
                );
    }

    public static int findFirstMissingNumberBinary(List<ConsecutiveEntity> list) {
        if (list == null || list.isEmpty())
            return 1;

        int left = 0;
        int right = list.size() - 1;

        // Si el primer elemento no es 1, falta el 1
        if (list.getFirst().getId().getNumber() > 1)
            return 1;

        // Si todos están consecutivos, el faltante es el siguiente al último
        if (list.get(right).getId().getNumber() == right + 1)
            return list.get(right).getId().getNumber() + 1;

        // Búsqueda binaria
        while (left < right) {
            int mid = (left + right) / 2;
            int expected = mid + 1;
            int actual = list.get(mid).getId().getNumber();

            if (actual == expected) {
                // El hueco está a la derecha
                left = mid + 1;
            } else {
                // El hueco está a la izquierda o en mid
                right = mid;
            }
        }

        // El primer faltante es el índice + 1
        return left + 1;
    }
}
