package com.itradingsolutions.itex.api.partners.suppliers.services.impl;

import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.partners.suppliers.exceptions.NotExistSupplierContactPhoneException;
import com.itradingsolutions.itex.api.partners.suppliers.models.dto.SupplierContactPhoneDTO;
import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierContactEntity;
import com.itradingsolutions.itex.api.partners.suppliers.models.entities.SupplierContactPhoneEntity;
import com.itradingsolutions.itex.api.partners.suppliers.models.mappers.SupplierContactPhoneMapper;
import com.itradingsolutions.itex.api.partners.suppliers.models.requests.SupplierContactRequest;
import com.itradingsolutions.itex.api.partners.suppliers.repository.ISupplierContactPhoneRepository;
import com.itradingsolutions.itex.api.partners.suppliers.services.ISupplierContactPhoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierContactPhoneServiceImpl extends UtilServiceAbs implements ISupplierContactPhoneService {

    private final ISupplierContactPhoneRepository supplierContactPhoneRepository;
    private final SupplierContactPhoneMapper supplierContactPhoneMapper;

    @Override
    @Transactional
    public List<SupplierContactPhoneDTO> saveListSupplierContactPhones(SupplierContactEntity supplierContactEntity, SupplierContactRequest supplierContactRequest, boolean isUpdate) {
        if (isUpdate && supplierContactEntity.getListPhones() != null) {
            List<SupplierContactPhoneEntity> deleteItems = supplierContactEntity.getListPhones()
                    .stream().filter(e ->
                            supplierContactRequest.getListPhones()
                                    .stream().noneMatch(r -> r.getId() != null && r.getId().equals(e.getId()))
                    ).toList();
            deleteItems.forEach(e -> supplierContactPhoneRepository.deleteBySupplierContactId(e.getId()));
        }
        List<SupplierContactPhoneDTO> resp = new ArrayList<>();
        supplierContactRequest.getListPhones().forEach(phone -> {
            SupplierContactPhoneEntity supplierContactPhoneEntity = new SupplierContactPhoneEntity();

            if (isUpdate && phone.getId() != null && supplierContactEntity.getListPhones() != null)
                supplierContactPhoneEntity = supplierContactEntity.getListPhones()
                        .stream()
                        .filter(item -> item.getId().equals(phone.getId()))
                        .findFirst().orElseThrow( () -> new NotExistSupplierContactPhoneException(compositeMessage("supplier.contact.phone.not-exist", new String[]{phone.getId().toString()})));

            supplierContactPhoneEntity.setSupplierContact(supplierContactEntity);
            supplierContactPhoneEntity.setType(phone.getType());
            supplierContactPhoneEntity.setPhoneNumber(phone.getPhoneNumber() == null ? null : phone.getPhoneNumber().trim());
            supplierContactPhoneEntity.setExt(phone.getExt() == null ? null : phone.getExt().trim());
            supplierContactPhoneEntity.setCityCode(phone.getCityCode() == null ? null : phone.getCityCode().trim());
            supplierContactPhoneEntity.setCountryCode(phone.getCountryCode() == null ? null : phone.getCountryCode().trim());
            supplierContactPhoneEntity.setValidMain(phone.isValidMain());
            resp.add(supplierContactPhoneMapper.entityToDTO(supplierContactPhoneRepository.save(supplierContactPhoneEntity)));
        });
        return resp;
    }
}
