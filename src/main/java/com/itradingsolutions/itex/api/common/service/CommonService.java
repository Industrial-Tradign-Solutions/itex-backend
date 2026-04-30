package com.itradingsolutions.itex.api.common.service;

import com.itradingsolutions.itex.api.common.models.dto.BaseDTO;

import java.util.List;
import java.util.UUID;

public interface CommonService<D extends BaseDTO> {
    List<D> listAll();
    void disable(UUID id);
    void enable(UUID id);
    D findById(UUID id, boolean validateActive);
    D create(D dto);
    D update(D dto, UUID id);
}
