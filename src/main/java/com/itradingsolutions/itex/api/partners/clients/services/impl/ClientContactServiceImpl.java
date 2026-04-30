package com.itradingsolutions.itex.api.partners.clients.services.impl;

import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.partners.clients.exceptions.NotExistClientContactException;
import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientContactDTO;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientContactEntity;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientInfoDepEntity;
import com.itradingsolutions.itex.api.partners.clients.models.mappers.ClientContactMapper;
import com.itradingsolutions.itex.api.partners.clients.models.requests.ClientInfoDepRequest;
import com.itradingsolutions.itex.api.partners.clients.repository.IClientContactRepository;
import com.itradingsolutions.itex.api.partners.clients.services.IClientContactPhoneService;
import com.itradingsolutions.itex.api.partners.clients.services.IClientContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientContactServiceImpl extends UtilServiceAbs implements IClientContactService {

    private final IClientContactRepository clientContactRepository;
    private final ClientContactMapper clientContactMapper;
    private final IClientContactPhoneService clientContactPhoneService;

    @Override
    @Transactional
    public List<ClientContactDTO> saveListClientContacts(ClientInfoDepEntity clientInfoDepEntity, ClientInfoDepRequest clientInfoDepRequest, boolean isUpdate) {
        if (clientInfoDepRequest.getListContacts() == null || clientInfoDepRequest.getListContacts().isEmpty()) return List.of();

        List<ClientContactDTO> resp = new ArrayList<>();
        clientInfoDepRequest.getListContacts().forEach(contact -> {
            ClientContactEntity clientContactEntity = new ClientContactEntity();
            clientContactEntity.setActive(true);
            if (isUpdate && contact.getId() != null)
                clientContactEntity = clientInfoDepEntity.getListContacts()
                        .stream()
                        .filter(item -> item.getId().equals(contact.getId()))
                        .findFirst().orElseThrow(() -> new NotExistClientContactException(compositeMessage("client.contact.not-exist", new String[]{contact.getId().toString()})));
            clientContactEntity.setClientInfoDep(clientInfoDepEntity);
            clientContactEntity.setName(normalizeText(capitalizeName(contact.getName())));
            clientContactEntity.setEmail(contact.getEmail() == null ? null : contact.getEmail().toLowerCase().trim());
            clientContactEntity.setTitle(normalizeText(capitalizeName(contact.getTitle())));
            clientContactEntity.setValidMain(contact.isValidMain());

            if (!contact.isActive()) {
                clientContactEntity.setActive(false);
                clientContactEntity.setValidMain(false);
            }

            clientContactEntity = clientContactRepository.save(clientContactEntity);
            var clientContactDTO = clientContactMapper.entityToDTO(clientContactEntity);
            clientContactDTO.setListPhones(clientContactPhoneService.saveListContactPhones(clientContactEntity, contact, isUpdate));
            resp.add(clientContactDTO);
        });
        return resp.stream().sorted(Comparator.comparing(ClientContactDTO::isActive).reversed()).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClientContactEntity findById(UUID id, UUID clientId) {
        return clientContactRepository.fetchByClientContactId(id, clientId)
                .orElseThrow( () -> new NotExistClientContactException(compositeMessage("client.contact.not-exist", new String[]{id.toString()})));
    }
}
