package com.itradingsolutions.itex.api.partners.suppliers.services.impl;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.admin.role.services.IRoleService;
import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import com.itradingsolutions.itex.api.common.models.enums.OpenAndLockType;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.masters.location.services.ICityService;
import com.itradingsolutions.itex.api.partners.suppliers.exceptions.NotActiveSupplierException;
import com.itradingsolutions.itex.api.partners.suppliers.exceptions.NotExistSupplierException;
import com.itradingsolutions.itex.api.partners.suppliers.exceptions.NotOpenSupplierException;
import com.itradingsolutions.itex.api.partners.suppliers.exceptions.SupplierErrorFormException;
import com.itradingsolutions.itex.api.partners.suppliers.models.dto.SupplierContactDTO;
import com.itradingsolutions.itex.api.partners.suppliers.models.enums.SupplierStatus;
import com.itradingsolutions.itex.api.partners.suppliers.models.mappers.SupplierMapper;
import com.itradingsolutions.itex.api.partners.suppliers.models.requests.SupplierInfoDepRequest;
import com.itradingsolutions.itex.api.partners.suppliers.repository.ISupplierRepository;
import com.itradingsolutions.itex.api.partners.suppliers.services.ISupplierInfoDepService;
import com.itradingsolutions.itex.api.partners.suppliers.models.dto.SupplierDTO;
import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierEntity;
import com.itradingsolutions.itex.api.partners.suppliers.models.filter.FilterListSuppliers;
import com.itradingsolutions.itex.api.partners.suppliers.models.requests.SupplierRequest;
import com.itradingsolutions.itex.api.partners.suppliers.services.ISupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class SupplierServiceImpl extends UtilServiceAbs implements ISupplierService {

    private final ISupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;
    private final IUserService userService;
    private final ICityService cityService;
    private final ISupplierInfoDepService supplierInfoDepService;
    

    @Override
    @Transactional
    public SupplierDTO createSupplier(SupplierRequest request) {
        validateMinContacts(request.getInfoByDepartment());
        var userAuthenticated = userService.getUserAuthenticated();

        SupplierEntity supplierEntity = new SupplierEntity();
        supplierEntity.setStatus(SupplierStatus.ACTIVE);
        supplierEntity.setCreatedAt(ZonedDateTime.now(zoneId));
        supplierEntity.setCreatedBy(userAuthenticated);
        supplierEntity.setOpenAt(ZonedDateTime.now(zoneId));
        supplierEntity.setOpenBy(userAuthenticated);
        return saveSupplierInfo(request, supplierEntity, false);
    }

    @Override
    @Transactional
    public SupplierDTO updateSupplier(SupplierRequest request, UUID supplierId) {
        validateMinContacts(request.getInfoByDepartment());
        var userAuthenticated = userService.getUserAuthenticated();
        SupplierEntity supplierEntity = findById(supplierId, false);
        validateOpenSupplier(supplierEntity, userAuthenticated);
        if (validChangeStatus(userAuthenticated)) {
            supplierEntity.setStatus(request.getStatus());
        }
        supplierEntity.setUpdatedAt(ZonedDateTime.now(zoneId));
        supplierEntity.setUpdatedBy(userAuthenticated);
        return saveSupplierInfo(request, supplierEntity, true);
    }

    @Override
    @Transactional
    public SupplierDTO openAndLockSupplier(UUID supplierId, OpenAndLockType type) {
        var supplier = findById(supplierId, false);
        if (supplier.getOpenBy() == null) {
            var user = userService.getUserAuthenticated();
            if (supplierRepository.countByOpenUserId(user.getId()) >= maxTabsOpen)
                throw new NotOpenSupplierException(compositeMessage("supplier.not-open-max", new String[]{maxTabsOpen.toString()}));
            if (type.equals(OpenAndLockType.EDIT)) {
                supplier.setOpenAt(ZonedDateTime.now(zoneId));
                supplier.setOpenBy(user);
                supplier = supplierRepository.save(supplier);
            }
        }
        var dto = supplierMapper.entityToDto(supplier);
        dto.getInfoByDepartment().forEach(info ->
                info.setListContacts(info.getListContacts().stream().sorted(Comparator.comparing(SupplierContactDTO::isActive).reversed()).toList())
        );
        return dto;
    }

    @Override
    @Transactional
    public void unlockSupplier(UUID supplierId) {
        var supplier = findById(supplierId, false);
        supplier.setOpenAt(null);
        supplier.setOpenBy(null);
        supplierRepository.save(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplierDTO> listAllSupplier(Pageable pageable, FilterListSuppliers filters) {
        Specification<SupplierEntity> spec = (filters == null ? Specification.where(null) : filters.filterSuppliers());
        Page<SupplierEntity> resp = supplierRepository.findAll(spec, pageable);
        return new PageImpl<>(resp.getContent().stream().map(supplierMapper::entityToDto).toList(),resp.getPageable(),resp.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierDTO> listAllOpenSupplier(String username) {
        List<SupplierEntity> list = username != null ? supplierRepository.fetchAllOpenByUsername(username) : supplierRepository.fetchAllOpen();
        return list.stream().map(supplierMapper::entityToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierEntity findSupplierById(UUID supplierId, boolean validateActive) {
        return findById(supplierId, validateActive);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierDTO> listAllActive() {
        var items = supplierRepository.fetchAllByStatus(SupplierStatus.ACTIVE);
        return items.stream().map(supplierMapper::entityToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Deprecated(since = "Funcion para eliminar")
    public long dashboardSuppliers() {
        return supplierRepository.countSuppliersByStatus(SupplierStatus.ACTIVE);
    }

    private SupplierDTO saveSupplierInfo(SupplierRequest request, SupplierEntity supplierEntity, boolean isUpdate) {
        if (request.getCityId() != null) {
            if (supplierEntity.getCity() == null || !supplierEntity.getCity().getId().equals(request.getCityId()))
                supplierEntity.setCity(cityService.findEntityById(request.getCityId()));
        } else {
            supplierEntity.setCity(null);
        }

        supplierEntity.setTaxId(request.getTaxId() != null ? request.getTaxId().trim() : null);
        supplierEntity.setName(removeSpecialChars(normalizeText(request.getName())).toUpperCase().trim());
        supplierEntity.setLanguage(request.getLanguage());
        supplierEntity.setPaymentMethod(request.getPaymentMethod());
        supplierEntity.setPaymentTerms(request.getPaymentTerms());
        supplierEntity.setAddress(capitalizeName(normalizeText(request.getAddress())));
        supplierEntity.setZipCode(request.getZipCode() == null ? null : request.getZipCode().trim());
        supplierEntity.setNotes(request.getNotes() == null ? null : normalizeText(request.getNotes()).trim());
        supplierEntity.setWireAchInstructions(request.getWireAchInstructions() != null ? normalizeText(request.getWireAchInstructions()).trim() : null);
        supplierEntity.setWebSite(request.getWebSite() != null ? normalizeText(request.getWebSite()).toLowerCase().trim() : null);
        supplierEntity.setCountryCode(request.getCountryCode() != null ? request.getCountryCode().trim() : null);
        supplierEntity.setCityCode(request.getCityCode() != null ? request.getCityCode().trim() : null);
        supplierEntity.setPhoneNumber(request.getPhoneNumber() != null ? request.getPhoneNumber().trim() : null);

        supplierEntity = supplierRepository.save(supplierEntity);
        var resp = supplierMapper.entityToDto(supplierEntity);

        resp.setInfoByDepartment(supplierInfoDepService.saveListSupplierInfoDep(request, supplierEntity, isUpdate));
        return resp;
    }

    private void validateOpenSupplier(SupplierEntity supplierEntity, UserEntity userAuthenticated) {
        if (supplierEntity.getOpenBy() == null)
            throw new NotOpenSupplierException(simpleMessage("supplier.not-block"));
        if ( !supplierEntity.getOpenBy().getId().equals(userAuthenticated.getId()))
            throw new NotOpenSupplierException(compositeMessage("supplier.not-block-by", new String[]{supplierEntity.getOpenBy().getFullName()}));
    }

    private void validateMinContacts(List<SupplierInfoDepRequest> supplierInfoDepRequests) {
        boolean validContacts = true;
        for (SupplierInfoDepRequest request: supplierInfoDepRequests) {
            if (request.getListContacts() != null && !request.getListContacts().isEmpty()) {
                validContacts = false;
                break;
            }
        }

        if (validContacts)
            throw new SupplierErrorFormException(simpleMessage("supplier.min-contacts"));
    }

    private boolean validChangeStatus(UserEntity user) {
        if (user.getRole().getId().equals(IRoleService.SUPER_ADMIN_ID))
            return true;

        return user.getRole().getActions().stream().anyMatch(roleAction ->
                roleAction.getAction().getId().equals(ModuleAction.CHANGE_STATUS_SUPPLIER.getId())
        );
    }

    private SupplierEntity findById(UUID id, boolean validateActive) {
        var supplier = supplierRepository.findById(id).orElseThrow(() ->
                new NotExistSupplierException(simpleMessage("supplier.not-exist"))
        );
        if (validateActive && supplier.getStatus().equals(SupplierStatus.INACTIVE))
            throw new NotActiveSupplierException(simpleMessage("supplier.not-active"));
        return supplier;
    }
}
