package com.itradingsolutions.itex.api.common.consecutive.services;

import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveDepartment;
import com.itradingsolutions.itex.api.common.consecutive.models.enums.ConsecutiveModule;

public interface IConsecutiveService {
    void saveConsecutive(ConsecutiveModule type , ConsecutiveDepartment department, String consecutive);
    void deleteConsecutive(ConsecutiveModule type, ConsecutiveDepartment department, String consecutive);
    String generateConsecutive(ConsecutiveModule module, ConsecutiveDepartment department, String clientCode);
}
