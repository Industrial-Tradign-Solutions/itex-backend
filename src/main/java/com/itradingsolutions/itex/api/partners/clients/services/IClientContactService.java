package com.itradingsolutions.itex.api.partners.clients.services;

import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientContactDTO;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientContactEntity;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientInfoDepEntity;
import com.itradingsolutions.itex.api.partners.clients.models.requests.ClientInfoDepRequest;

import java.util.List;
import java.util.UUID;

public interface IClientContactService {

    List<ClientContactDTO> saveListClientContacts(ClientInfoDepEntity clientInfoDepEntity, ClientInfoDepRequest clientInfoDepRequest, boolean isUpdate);
    ClientContactEntity findById(UUID id, UUID clientId);
}
