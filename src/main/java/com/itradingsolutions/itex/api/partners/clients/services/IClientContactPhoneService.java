package com.itradingsolutions.itex.api.partners.clients.services;

import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientContactPhoneDTO;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientContactEntity;
import com.itradingsolutions.itex.api.partners.clients.models.requests.ClientContactRequest;

import java.util.List;

public interface IClientContactPhoneService {

    List<ClientContactPhoneDTO> saveListContactPhones(ClientContactEntity clientContactEntity, ClientContactRequest clientContactRequest, boolean isUpdate);
}
