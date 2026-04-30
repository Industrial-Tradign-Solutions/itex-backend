package com.itradingsolutions.itex.api.partners.suppliers.services.impl;

import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.partners.suppliers.exceptions.NotExistSupplierContactException;
import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierContactEntity;
import com.itradingsolutions.itex.api.partners.suppliers.models.mappers.SupplierContactMapper;
import com.itradingsolutions.itex.api.partners.suppliers.repository.ISupplierContactRepository;
import com.itradingsolutions.itex.api.partners.suppliers.services.ISupplierContactPhoneService;
import com.itradingsolutions.itex.api.partners.suppliers.models.dto.SupplierContactDTO;
import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierInfoDepEntity;
import com.itradingsolutions.itex.api.partners.suppliers.models.requests.SupplierInfoDepRequest;
import com.itradingsolutions.itex.api.partners.suppliers.services.ISupplierContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupplierContactServiceImpl extends UtilServiceAbs implements ISupplierContactService {

    private final ISupplierContactRepository supplierContactRepository;
    private final SupplierContactMapper supplierContactMapper;
    private final ISupplierContactPhoneService supplierContactPhoneService;

    @Override
    @Transactional
    public List<SupplierContactDTO> saveListSupplierContacts(SupplierInfoDepEntity supplierInfoDepEntity, SupplierInfoDepRequest supplierInfoDepRequest, boolean isUpdate) {
        if (supplierInfoDepRequest.getListContacts() == null || supplierInfoDepRequest.getListContacts().isEmpty())
            return List.of();
        
        List<SupplierContactDTO> resp = new ArrayList<>();
        supplierInfoDepRequest.getListContacts().forEach(contact -> {
            SupplierContactEntity supplierContactEntity = new SupplierContactEntity();
            supplierContactEntity.setActive(true);
            if (isUpdate && contact.getId() != null)
                supplierContactEntity = supplierInfoDepEntity.getListContacts()
                        .stream()
                        .filter(item -> item.getId().equals(contact.getId()))
                        .findFirst().orElseThrow(() -> new NotExistSupplierContactException(compositeMessage("supplier.contact.not-exist", new String[]{contact.getId().toString()})));
            supplierContactEntity.setSupplierInfoDep(supplierInfoDepEntity);
            supplierContactEntity.setName(normalizeText(capitalizeName(contact.getName())));
            supplierContactEntity.setEmail(contact.getEmail() == null ? null : contact.getEmail().toLowerCase().trim());
            supplierContactEntity.setTitle(normalizeText(capitalizeName(contact.getTitle())));
            supplierContactEntity.setValidMain(contact.isValidMain());

            if (!contact.isActive()) {
                supplierContactEntity.setActive(false);
                supplierContactEntity.setValidMain(false);
            }

            supplierContactEntity = supplierContactRepository.save(supplierContactEntity);
            var supplierContactDTO = supplierContactMapper.entityToDTO(supplierContactEntity);
            supplierContactDTO.setListPhones(supplierContactPhoneService.saveListSupplierContactPhones(supplierContactEntity, contact, isUpdate));

            resp.add(supplierContactDTO);
        });


        return resp.stream().sorted(Comparator.comparing(SupplierContactDTO::isActive).reversed()).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierContactEntity findById(UUID id, UUID supplierId) {
        return supplierContactRepository.fetchBySupplierContactId(id, supplierId)
                .orElseThrow( () -> new NotExistSupplierContactException(compositeMessage("supplier.contact.not-exist", new String[]{id.toString()})));
    }
}
