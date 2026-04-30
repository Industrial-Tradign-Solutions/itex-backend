package com.itradingsolutions.itex.api.masters.location.services.impl;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;
import com.itradingsolutions.itex.api.masters.location.exceptions.NotExistCityException;
import com.itradingsolutions.itex.api.masters.location.models.dto.CityDTO;
import com.itradingsolutions.itex.api.masters.location.models.entities.CityEntity;
import com.itradingsolutions.itex.api.masters.location.models.mappers.CityMapper;
import com.itradingsolutions.itex.api.masters.location.repositories.ICityRepository;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.masters.location.services.ICityService;
import com.itradingsolutions.itex.api.masters.location.services.IStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class CityServiceImpl extends UtilServiceAbs implements ICityService {

    private final ICityRepository cityRepository;
    private final CityMapper cityMapper;
    private final IStateService stateService;

    @Override
    @Transactional(readOnly = true)
    public CityDTO findById(UUID cityId, boolean validateActive) {
        return cityMapper.entityToDto(getCityById(cityId));
    }

    @Override
    @Transactional(readOnly = true)
    public CityEntity findEntityById(UUID cityId) {
        return getCityById(cityId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CityDTO> listAll() {
        return cityRepository.fetchAll().stream().map(cityMapper::entityToDto).toList();
    }

    @Override
    public void disable(UUID id) {
        throw new NotFoundException(NOT_FOUND_MESSAGE);
    }

    @Override
    public void enable(UUID id) {
        throw new NotFoundException(NOT_FOUND_MESSAGE);
    }

    @Override
    @Transactional
    public CityDTO create(CityDTO city) {
        var entity = new CityEntity();
        entity.setActive(true);
        return createOrUpdate(city, entity);
    }

    @Override
    @Transactional
    public CityDTO update(CityDTO city, UUID id) {
        return createOrUpdate(city, getCityById(id));
    }

    private CityDTO createOrUpdate(CityDTO city, CityEntity cityEntity) {
        cityEntity.setName(city.getName());

        if(cityEntity.getState() == null || !cityEntity.getState().getId().equals(city.getState().getId()))
            cityEntity.setState(stateService.findEntityById(city.getState().getId()));

        cityEntity.setLatitude(city.getLatitude().trim());
        cityEntity.setLongitude(city.getLongitude().trim());
        return cityMapper.entityToDto(cityRepository.save(cityEntity));
    }

    private CityEntity getCityById(UUID id) {
        return cityRepository.findById(id).orElseThrow(() ->
                new NotExistCityException(simpleMessage("city.not-exist"))
        );
    }
}
