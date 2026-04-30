package com.itradingsolutions.itex.api.partners.suppliers.services.impl;

import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.masters.department.services.IDepartmentService;
import com.itradingsolutions.itex.api.partners.suppliers.exceptions.NotExistSupplierDepInfoException;
import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierInfoDepEntity;
import com.itradingsolutions.itex.api.partners.suppliers.models.mappers.SupplierInfoDepMapper;
import com.itradingsolutions.itex.api.partners.suppliers.repository.ISupplierInfoDepRepository;
import com.itradingsolutions.itex.api.partners.suppliers.services.ISupplierContactService;
import com.itradingsolutions.itex.api.partners.suppliers.models.dto.SupplierInfoDepDTO;
import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierEntity;
import com.itradingsolutions.itex.api.partners.suppliers.models.requests.SupplierRequest;
import com.itradingsolutions.itex.api.partners.suppliers.services.ISupplierInfoDepService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierInfoDepServiceImpl extends UtilServiceAbs implements ISupplierInfoDepService {

    private final ISupplierInfoDepRepository supplierInfoDepRepository;
    private final IDepartmentService departmentService;
    private final SupplierInfoDepMapper supplierInfoDepMapper;
    private final ISupplierContactService supplierContactService;

    @Override
    @Transactional
    public List<SupplierInfoDepDTO> saveListSupplierInfoDep(SupplierRequest request, SupplierEntity supplierEntity, boolean isUpdate) {
        List<SupplierInfoDepDTO> resp = new ArrayList<>();

        request.getInfoByDepartment().forEach(supplierInfoDepRequest -> {
            SupplierInfoDepEntity supplierInfoDepEntity = new SupplierInfoDepEntity();

            if (isUpdate) {
                if (supplierInfoDepRequest.getId() != null) {
                    supplierInfoDepEntity = supplierEntity.getInfoByDepartment()
                            .stream()
                            .filter(item -> item.getId().equals(supplierInfoDepRequest.getId()))
                            .findFirst().orElseThrow(() ->
                                    new NotExistSupplierDepInfoException(compositeMessage("supplier.dep-info.not-exist", new String[]{supplierInfoDepRequest.getId().toString()}))
                            );

                } else {
                    supplierInfoDepEntity = supplierEntity.getInfoByDepartment()
                            .stream()
                            .filter(item -> item.getDepartment().getId().equals(supplierInfoDepRequest.getDepartmentId()))
                            .findFirst().orElse(new SupplierInfoDepEntity());
                }
            }
            supplierInfoDepEntity.setSupplier(supplierEntity);
            supplierInfoDepEntity.setNotes(supplierInfoDepRequest.getNotes() != null ? normalizeText(supplierInfoDepRequest.getNotes()).trim() : null);

            if (supplierInfoDepEntity.getDepartment() == null)
                supplierInfoDepEntity.setDepartment(departmentService.findEntityById(supplierInfoDepRequest.getDepartmentId(), true));

            var supplierInfoDepDTO = supplierInfoDepMapper.entityToDTO(supplierInfoDepRepository.save(supplierInfoDepEntity));
            supplierInfoDepDTO.setListContacts(supplierContactService.saveListSupplierContacts(supplierInfoDepEntity, supplierInfoDepRequest, isUpdate));

            resp.add(supplierInfoDepDTO);
        });
        
        
        return resp;
    }
}
