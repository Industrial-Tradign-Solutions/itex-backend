package com.itradingsolutions.itex.api.masters.location.services.impl;

import com.itradingsolutions.itex.api.common.util.exceptions.NotFoundException;
import com.itradingsolutions.itex.api.masters.location.exceptions.NotExistCountryException;
import com.itradingsolutions.itex.api.masters.location.models.dto.CountryDTO;
import com.itradingsolutions.itex.api.masters.location.models.entities.CountryEntity;
import com.itradingsolutions.itex.api.masters.location.models.mappers.CountryMapper;
import com.itradingsolutions.itex.api.masters.location.repositories.ICountryRepository;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.masters.location.services.ICountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class CountryServiceImpl extends UtilServiceAbs implements ICountryService {

    private final ICountryRepository countryRepository;
    private final CountryMapper countryMapper;

    @Override
    @Transactional(readOnly = true)
    public CountryEntity findEntityById(UUID countryId) {
        return getCountryById(countryId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CountryDTO> findByNameOrNameShort(String name) {
        return countryRepository.fetchOneByNameOrNameShort(name).map(countryMapper::entityToDto);
    }

    @Override
    @Transactional
    public CountryDTO create(CountryDTO request) {
        var entity = new CountryEntity();
        entity.setActive(true);
        return createOrUpdate(request, entity);
    }

    @Override
    @Transactional
    public CountryDTO update(CountryDTO request, UUID id) {
        return createOrUpdate(request, getCountryById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public CountryDTO findById(UUID id, boolean validateActive) {
        return countryMapper.entityToDto(getCountryById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CountryDTO> listAll() {
        return countryRepository.fetchAll().stream().map(countryMapper::entityToDto).toList();
    }

    @Override
    public void disable(UUID id) {
        throw new NotFoundException(NOT_FOUND_MESSAGE);
    }

    @Override
    public void enable(UUID id) {
        throw new NotFoundException(NOT_FOUND_MESSAGE);
    }

    private CountryDTO createOrUpdate(CountryDTO request, CountryEntity countryEntity) {
        countryEntity.setName(request.getName().toUpperCase().trim());
        countryEntity.setNameShort(request.getNameShort().trim().toUpperCase());
        countryEntity.setLatitude(request.getLatitude().trim());
        countryEntity.setLongitude(request.getLongitude().trim());
        return countryMapper.entityToDto(countryRepository.save(countryEntity));
    }

    private CountryEntity getCountryById(UUID id) {
        return countryRepository.findById(id).orElseThrow(() ->
                new NotExistCountryException("The consulted country does not exist")
        );
    }
}
