package com.itradingsolutions.itex.api.masters.department.services.impl;

import com.itradingsolutions.itex.api.common.util.exceptions.BadRequestException;
import com.itradingsolutions.itex.api.masters.department.exceptions.NotDepartmentActiveException;
import com.itradingsolutions.itex.api.masters.department.exceptions.NotExistDepartmentException;
import com.itradingsolutions.itex.api.masters.department.models.dto.DepartmentDTO;
import com.itradingsolutions.itex.api.masters.department.models.entities.DepartmentEntity;
import com.itradingsolutions.itex.api.masters.department.models.mappers.DepartmentMapper;
import com.itradingsolutions.itex.api.masters.department.repositories.IDepartmentRepository;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.masters.department.services.IDepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl extends UtilServiceAbs implements IDepartmentService {

    private final IDepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentDTO> listAll() {
        var departments = departmentRepository.findAll();
        return departments.stream().map(departmentMapper::entityToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentDTO> listAllShowInfo(boolean isShowOnlyClientInfo, boolean isShowOnlySupplierInfo) {
        if (isShowOnlyClientInfo == isShowOnlySupplierInfo)
            throw new BadRequestException(simpleMessage("department.show-info.not-params"));

        var departments = isShowOnlyClientInfo
                            ? departmentRepository.fetchAllShowInfoClient()
                            : departmentRepository.fetchAllShowInfoSupplier();

        return departments.stream().map(departmentMapper::entityToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentEntity findEntityById(UUID id, boolean validateActive) {
        return getDepartmentById(id, validateActive);
    }

    @Override
    @Transactional
    public DepartmentDTO create(DepartmentDTO request) {
        DepartmentEntity department = new DepartmentEntity();
        department.setActive(true);
        return createOrUpdate(request, department);
    }

    @Override
    @Transactional
    public DepartmentDTO update(DepartmentDTO request, UUID departmentId) {
        return createOrUpdate(request, getDepartmentById(departmentId, true));
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentDTO findById(UUID departmentId, boolean validateActive) {
        return departmentMapper.entityToDto(getDepartmentById(departmentId, validateActive));
    }


    @Override
    @Transactional
    public void disable(UUID departmentId) {
        var department = getDepartmentById(departmentId, true);
        department.setActive(false);
        departmentRepository.save(department);
    }

    @Override
    @Transactional
    public void enable(UUID departmentId) {
        var department = getDepartmentById(departmentId, false);
        if (department.isActive()) throw new NotDepartmentActiveException("The department is active");
        department.setActive(true);
        departmentRepository.save(department);
    }

    private DepartmentDTO createOrUpdate(DepartmentDTO request, DepartmentEntity department) {
        department.setName(request.getName());
        department.setClientInfo(request.isClientInfo());
        department.setSupplierInfo(request.isSupplierInfo());
        department.setDescription(request.getDescription());
        return departmentMapper.entityToDto(departmentRepository.save(department));
    }

    private DepartmentEntity getDepartmentById(UUID departmentId, boolean validateActive) {
        var department = departmentRepository.findById(departmentId).orElseThrow(() ->
                new NotExistDepartmentException("The consulted department does not exist")
        );
        if (validateActive && !department.isActive())
            throw new NotDepartmentActiveException("The department is not active");
        return department;
    }
}
