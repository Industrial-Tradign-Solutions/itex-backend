package com.itradingsolutions.itex.api.masters.industry.services.impl;

import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.masters.industry.exceptions.NotExistIndustryException;
import com.itradingsolutions.itex.api.masters.industry.exceptions.NotIndustryActiveException;
import com.itradingsolutions.itex.api.masters.industry.models.dto.IndustryDTO;
import com.itradingsolutions.itex.api.masters.industry.models.entities.IndustryEntity;
import com.itradingsolutions.itex.api.masters.industry.models.mappers.IndustryMapper;
import com.itradingsolutions.itex.api.masters.industry.repositories.IIndustryRepository;
import com.itradingsolutions.itex.api.masters.industry.services.IIndustryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IndustryServiceImpl extends UtilServiceAbs implements IIndustryService {

    private final IIndustryRepository industryRepository;
    private final IndustryMapper industryMapper;

    @Override
    @Transactional(readOnly = true)
    public IndustryEntity findEntityById(UUID id, boolean validateActive) {
        return getIndustryById(id, validateActive);
    }

    @Override
    @Transactional
    public IndustryDTO create(IndustryDTO request) {
        IndustryEntity industry = new IndustryEntity();
        industry.setActive(true);
        return createOrUpdate(request, industry);
    }

    @Override
    @Transactional
    public IndustryDTO update(IndustryDTO request, UUID id) {
        return createOrUpdate(request, getIndustryById(id, true));
    }

    @Override
    @Transactional(readOnly = true)
    public IndustryDTO findById(UUID industryId, boolean validateActive) {
        return industryMapper.entityToDto(getIndustryById(industryId, validateActive));
    }

    @Override
    @Transactional(readOnly = true)
    public List<IndustryDTO> listAll() {
        return industryRepository.fetchAll().stream().map(industryMapper::entityToDto).toList();
    }

    @Override
    @Transactional
    public void disable(UUID industryId) {
        var industry = getIndustryById(industryId, true);
        industry.setActive(false);
        industryRepository.save(industry);
    }

    @Override
    @Transactional
    public void enable(UUID industryId) {
        var industry = getIndustryById(industryId, false);
        if (industry.isActive()) throw new NotIndustryActiveException(simpleMessage("industry.active"));
        industry.setActive(true);
        industryRepository.save(industry);
    }

    private IndustryDTO createOrUpdate(IndustryDTO request, IndustryEntity industry) {
        industry.setDescription(request.getDescription());
        industry.setName(request.getName());
        return industryMapper.entityToDto(industryRepository.save(industry));
    }

    private IndustryEntity getIndustryById(UUID industryId, boolean validateActive) {
         var industry = industryRepository.findById(industryId).orElseThrow(() ->
                new NotExistIndustryException(simpleMessage("industry.not.found"))
        );
         if (validateActive && !industry.isActive())
             throw new NotIndustryActiveException(simpleMessage("industry.not.active"));
        return industry;
    }
}
