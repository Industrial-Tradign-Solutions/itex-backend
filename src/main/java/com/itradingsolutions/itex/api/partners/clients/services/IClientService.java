package com.itradingsolutions.itex.api.partners.clients.services;

import com.itradingsolutions.itex.api.common.models.enums.OpenAndLockType;
import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientDTO;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientEntity;
import com.itradingsolutions.itex.api.partners.clients.models.enums.ClientStatus;
import com.itradingsolutions.itex.api.partners.clients.models.filter.FilterListClients;
import com.itradingsolutions.itex.api.partners.clients.models.requests.ClientRequest;
import com.itradingsolutions.itex.api.partners.clients.models.responses.ClientDashboardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IClientService {

    ClientDTO createClient(ClientRequest request);
    ClientDTO updateClient(ClientRequest request, UUID prospectId);
    ClientDTO openAndLockClient(UUID clientId, OpenAndLockType type);
    void unlockClient(UUID clientId);
    Page<ClientDTO> listAllClients(Pageable pageable, FilterListClients filters);
    List<ClientDTO> listAllOpenClients(String username);
    ClientEntity findClientById(UUID clientId, boolean validateActive);

    @Deprecated(since = "Funcion para eliminar")
    ClientDashboardResponse dashboardClients();

    List<ClientDTO> listAllByStatus(ClientStatus status);
    List<ClientDTO> listAllWhitMissingInfo();
    List<ClientDTO> listAllActive();
}
