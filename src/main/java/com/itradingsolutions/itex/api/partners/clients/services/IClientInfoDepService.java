package com.itradingsolutions.itex.api.partners.clients.services;

import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientInfoDepDTO;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientEntity;
import com.itradingsolutions.itex.api.partners.clients.models.requests.ClientRequest;

import java.util.List;

public interface IClientInfoDepService {
    List<ClientInfoDepDTO> saveListClientInfoDep(ClientRequest request, ClientEntity clientEntity, boolean isUpdate);
}
