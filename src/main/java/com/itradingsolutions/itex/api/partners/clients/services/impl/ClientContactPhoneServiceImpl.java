package com.itradingsolutions.itex.api.partners.clients.services.impl;

import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.partners.clients.exceptions.NotExistClientContactPhoneException;
import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientContactPhoneDTO;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientContactEntity;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientContactPhoneEntity;
import com.itradingsolutions.itex.api.partners.clients.models.mappers.ClientContactPhoneMapper;
import com.itradingsolutions.itex.api.partners.clients.models.requests.ClientContactRequest;
import com.itradingsolutions.itex.api.partners.clients.repository.IClientContactPhoneRepository;
import com.itradingsolutions.itex.api.partners.clients.services.IClientContactPhoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientContactPhoneServiceImpl extends UtilServiceAbs implements IClientContactPhoneService {

    private final IClientContactPhoneRepository clientContactPhoneRepository;
    private final ClientContactPhoneMapper clientContactPhoneMapper;

    @Override
    @Transactional
    public List<ClientContactPhoneDTO> saveListContactPhones(ClientContactEntity clientContactEntity, ClientContactRequest clientContactRequest, boolean isUpdate) {
        if (isUpdate && clientContactEntity.getListPhones() != null) {
            List<ClientContactPhoneEntity> deleteItems = clientContactEntity.getListPhones()
                    .stream().filter(e ->
                            clientContactRequest.getListPhones()
                                    .stream().noneMatch(r -> r.getId() != null && r.getId().equals(e.getId()))
                    ).toList();
            deleteItems.forEach(e -> clientContactPhoneRepository.deleteByClientContactId(e.getId()));
        }
        List<ClientContactPhoneDTO> resp = new ArrayList<>();
        clientContactRequest.getListPhones().forEach(phone -> {
            ClientContactPhoneEntity clientContactPhoneEntity = new ClientContactPhoneEntity();

            if (isUpdate && phone.getId() != null && clientContactEntity.getListPhones() != null)
                clientContactPhoneEntity = clientContactEntity.getListPhones()
                        .stream()
                        .filter(item -> item.getId().equals(phone.getId()))
                        .findFirst().orElseThrow( () -> new NotExistClientContactPhoneException(compositeMessage("client.contact.phone.not-exist", new String[]{phone.getId().toString()})));

            clientContactPhoneEntity.setClientContact(clientContactEntity);
            clientContactPhoneEntity.setType(phone.getType());
            clientContactPhoneEntity.setPhoneNumber(phone.getPhoneNumber() == null ? null : phone.getPhoneNumber().trim());
            clientContactPhoneEntity.setExt(phone.getExt() != null ? phone.getExt().trim() : null);
            clientContactPhoneEntity.setCityCode(phone.getCityCode() != null ? phone.getCityCode().trim() : null);
            clientContactPhoneEntity.setCountryCode(phone.getCountryCode() != null ? phone.getCountryCode().trim() : null);
            clientContactPhoneEntity.setValidMain(phone.isValidMain());
            resp.add(clientContactPhoneMapper.entityToDTO(clientContactPhoneRepository.save(clientContactPhoneEntity)));
        });
        return resp;
    }
}
