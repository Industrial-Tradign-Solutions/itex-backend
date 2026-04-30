package com.itradingsolutions.itex.api.masters.location.services.impl;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;
import com.itradingsolutions.itex.api.masters.location.exceptions.NotExistStateException;
import com.itradingsolutions.itex.api.masters.location.models.dto.StateDTO;
import com.itradingsolutions.itex.api.masters.location.models.entities.StateEntity;
import com.itradingsolutions.itex.api.masters.location.models.mappers.StateMapper;
import com.itradingsolutions.itex.api.masters.location.repositories.IStateRepository;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.masters.location.services.ICountryService;
import com.itradingsolutions.itex.api.masters.location.services.IStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class StateServiceImpl extends UtilServiceAbs implements IStateService {

    private final IStateRepository stateRepository;
    private final StateMapper stateMapper;
    private final ICountryService countryService;

    @Override
    @Transactional(readOnly = true)
    public StateEntity findEntityById(UUID stateId) {
        return getStateById(stateId);
    }

    @Override
    @Transactional
    public StateDTO create(StateDTO state) {
        var entity = new StateEntity();
        entity.setActive(true);
        return createOrUpdate(state, entity);
    }

    @Override
    @Transactional
    public StateDTO update(StateDTO state, UUID id) {
        return createOrUpdate(state, getStateById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public StateDTO findById(UUID id, boolean validateActive) {
        return stateMapper.entityToDto(getStateById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StateDTO> listAll() {
        return stateRepository.fetchAll().stream().map(stateMapper::entityToDto).toList();
    }

    @Override
    public void disable(UUID id) {
        throw new NotFoundException(NOT_FOUND_MESSAGE);
    }

    @Override
    public void enable(UUID id) {
        throw new NotFoundException(NOT_FOUND_MESSAGE);
    }

    private StateDTO createOrUpdate(StateDTO state, StateEntity stateEntity) {
        stateEntity.setName(state.getName().toUpperCase().trim());

        if(stateEntity.getCountry() == null || !stateEntity.getCountry().getId().equals(state.getCountry().getId()))
            stateEntity.setCountry(countryService.findEntityById(state.getCountry().getId()));

        stateEntity.setNameShort(state.getNameShort().trim().toUpperCase());
        stateEntity.setLatitude(state.getLatitude().trim());
        stateEntity.setLongitude(state.getLongitude().trim());
        stateEntity.setShowShortName(state.isShowShortName());
        return stateMapper.entityToDto(stateRepository.save(stateEntity));
    }

    private StateEntity getStateById(UUID id) {
        return stateRepository.findById(id).orElseThrow(() ->
                new NotExistStateException(simpleMessage("state.not-exist"))
        );
    }
}
