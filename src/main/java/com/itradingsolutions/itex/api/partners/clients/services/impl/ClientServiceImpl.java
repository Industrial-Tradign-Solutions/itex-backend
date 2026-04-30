package com.itradingsolutions.itex.api.partners.clients.services.impl;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import com.itradingsolutions.itex.api.common.models.enums.OpenAndLockType;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.masters.industry.services.IIndustryService;
import com.itradingsolutions.itex.api.masters.location.services.ICityService;
import com.itradingsolutions.itex.api.partners.clients.exceptions.ClientErrorFormException;
import com.itradingsolutions.itex.api.partners.clients.exceptions.NotActiveClientException;
import com.itradingsolutions.itex.api.partners.clients.exceptions.NotExistClientException;
import com.itradingsolutions.itex.api.partners.clients.exceptions.NotOpenClientException;
import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientContactDTO;
import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientDTO;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientEntity;
import com.itradingsolutions.itex.api.partners.clients.models.enums.ClientStatus;
import com.itradingsolutions.itex.api.partners.clients.models.filter.FilterListClients;
import com.itradingsolutions.itex.api.partners.clients.models.mappers.ClientMapper;
import com.itradingsolutions.itex.api.partners.clients.models.requests.ClientInfoDepRequest;
import com.itradingsolutions.itex.api.partners.clients.models.requests.ClientRequest;
import com.itradingsolutions.itex.api.partners.clients.models.responses.ClientDashboardResponse;
import com.itradingsolutions.itex.api.partners.clients.repository.IClientRepository;
import com.itradingsolutions.itex.api.partners.clients.services.IClientInfoDepService;
import com.itradingsolutions.itex.api.partners.clients.services.IClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl extends UtilServiceAbs implements IClientService {

    private final IClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final IUserService userService;
    private final ICityService cityService;
    private final IIndustryService industryService;
    private final IClientInfoDepService clientInfoDepService;

    @Override
    @Transactional
    public ClientDTO createClient(ClientRequest request) {
        validateMinContacts(request.getInfoByDepartment());
        var userAuthenticated = userService.getUserAuthenticated();

        ClientEntity clientEntity = new ClientEntity();
        clientEntity.setStatus(ClientStatus.PROSPECT);
        clientEntity.setCreatedAt(ZonedDateTime.now(zoneId));
        clientEntity.setCreatedBy(userAuthenticated);
        clientEntity.setOpenAt(ZonedDateTime.now(zoneId));
        clientEntity.setOpenBy(userAuthenticated);
        return saveClientInfo(request, clientEntity, false);
    }

    @Override
    @Transactional
    public ClientDTO updateClient(ClientRequest request, UUID prospectId) {
        validateMinContacts(request.getInfoByDepartment());

        var userAuthenticated = userService.getUserAuthenticated();
        ClientEntity clientEntity = findById(prospectId, false);

        validateOpenClient(clientEntity, userAuthenticated);

        if (validateAction(userAuthenticated, ModuleAction.CHANGE_STATUS_CLIENT)) {
            if (request.getStatus() == ClientStatus.ACTIVE) {
                validateInfoClient(request);
                if (clientEntity.getChangeProspectToClientAt() == null && clientEntity.getChangeProspectToClientBy() == null) {
                    clientEntity.setChangeProspectToClientBy(userAuthenticated);
                    clientEntity.setChangeProspectToClientAt(ZonedDateTime.now(zoneId));
                }
            } else if (request.getStatus() == ClientStatus.PROSPECT && (clientEntity.getChangeProspectToClientAt() != null || clientEntity.getChangeProspectToClientBy() != null)) {
                clientEntity.setChangeProspectToClientBy(null);
                clientEntity.setChangeProspectToClientAt(null);
            }
            clientEntity.setStatus(request.getStatus());
        }

        clientEntity.setUpdatedAt(ZonedDateTime.now(zoneId));
        clientEntity.setUpdatedBy(userAuthenticated);

        return saveClientInfo(request, clientEntity, true);
    }

    @Override
    @Transactional
    public ClientDTO openAndLockClient(UUID clientId, OpenAndLockType type) {
        var client = findById(clientId, false);
        if (client.getOpenBy() == null) {
            var user = userService.getUserAuthenticated();
            if (clientRepository.countByOpenUserId(user.getId()) >= maxTabsOpen)
                throw new NotOpenClientException(compositeMessage("client.not-open-max", new String[]{maxTabsOpen.toString()}));
            if (type.equals(OpenAndLockType.EDIT)) {
                client.setOpenAt(ZonedDateTime.now(zoneId));
                client.setOpenBy(user);
                client = clientRepository.save(client);
            }
        }
        var dto = clientMapper.entityToDto(client);
        dto.getInfoByDepartment().forEach(info ->
                info.setListContacts(info.getListContacts().stream().sorted(Comparator.comparing(ClientContactDTO::isActive).reversed()).toList())
        );
        return dto;
    }

    @Override
    @Transactional
    public void unlockClient(UUID clientId) {
        var client = findById(clientId, false);
        client.setOpenAt(null);
        client.setOpenBy(null);
        clientRepository.save(client);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientDTO> listAllClients(Pageable pageable, FilterListClients filters) {
        Specification<ClientEntity> spec = (filters == null ? Specification.where(null) : filters.filterClient());
        Page<ClientEntity> resp = clientRepository.findAll(spec, pageable);
        return new PageImpl<>(resp.getContent().stream().map(clientMapper::entityToDto).toList(),resp.getPageable(),resp.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientDTO> listAllOpenClients(String username) {
        List<ClientEntity> list = username != null ? clientRepository.fetchAllOpenByUsername(username) : clientRepository.fetchAllOpen();
        return list.stream().map(clientMapper::entityToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClientEntity findClientById(UUID clientId, boolean validateActive) {
        return findById(clientId, validateActive);
    }

    @Override
    @Transactional(readOnly = true)
    @Deprecated(since = "Funcion para eliminar")
    public ClientDashboardResponse dashboardClients() {
        var totalClients = clientRepository.countClientsByStatus(ClientStatus.ACTIVE);
        var totalProspects = clientRepository.countClientsByStatus(ClientStatus.PROSPECT);
        return new ClientDashboardResponse(totalClients, totalProspects);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientDTO> listAllByStatus(ClientStatus status) {
        List<ClientEntity> list = clientRepository.fetchAllByStatus(status);
        return list.stream().map(clientMapper::entityToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientDTO> listAllWhitMissingInfo() {
        List<ClientEntity> list = clientRepository.fetchAllByStatus(ClientStatus.ACTIVE);
        List<ClientEntity> clients = new ArrayList<>();
        for (var client : list) {
            if (client.getAddress() == null || client.getAddress().isEmpty()) {
                clients.add(client);
                break;
            }
            if (client.getCity() == null) {
                clients.add(client);
                break;
            }
            boolean addClient = false;
            for (var info: client.getInfoByDepartment()) {
                for (var contact: info.getListContacts()) {
                    if (contact.getEmail() == null || contact.getEmail().isEmpty()) {
                        addClient = true;
                        break;
                    }
                    for (var phone: contact.getListPhones()) {
                        if (phone.getCountryCode() == null || phone.getCountryCode().isEmpty()) {
                            addClient = true;
                            break;
                        }
                        if (phone.getPhoneNumber() == null || phone.getPhoneNumber().isEmpty()) {
                            addClient = true;
                            break;
                        }
                    }
                }
            }
            if (addClient) {
                clients.add(client);
            }
        }
        return clients.stream().map(clientMapper::entityToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientDTO> listAllActive() {
        var items = clientRepository.fetchAllByStatus(ClientStatus.ACTIVE);
        return items.stream().map(clientMapper::entityToDto).toList();
    }

    private ClientDTO saveClientInfo(ClientRequest request, ClientEntity clientEntity, boolean isUpdate) {

        if (request.getCityId() != null) {
            if (clientEntity.getCity() == null || !clientEntity.getCity().getId().equals(request.getCityId()))
                clientEntity.setCity(cityService.findEntityById(request.getCityId()));
        } else {
            clientEntity.setCity(null);
        }

        if (request.getIndustryId() != null && (clientEntity.getIndustry() == null || !clientEntity.getIndustry().getId().equals(request.getIndustryId())))
            clientEntity.setIndustry(industryService.findEntityById(request.getIndustryId(), true));

        clientEntity.setCode(normalizeText(request.getCode()).toUpperCase().trim());
        clientEntity.setName(removeSpecialChars(normalizeText(request.getName())).toUpperCase().trim());
        clientEntity.setTaxId(request.getTaxId() != null ? request.getTaxId().toUpperCase().toUpperCase().trim() : null);
        clientEntity.setLanguage(request.getLanguage());
        clientEntity.setPaymentMethod(request.getPaymentMethod());
        clientEntity.setPaymentTerms(request.getPaymentTerms());
        clientEntity.setAddress(normalizeText(capitalizeName(request.getAddress())));
        clientEntity.setZipCode(request.getZipCode() != null ? request.getZipCode().toUpperCase().trim() : null);
        clientEntity.setNotes(request.getNotes() != null ? normalizeText(request.getNotes()).trim() : null);
        clientEntity.setWebSite(request.getWebSite() != null ? normalizeText(request.getWebSite()).toLowerCase().trim() : null);
        clientEntity.setCountryCode(request.getCountryCode() != null ? request.getCountryCode().toLowerCase().trim() : null);
        clientEntity.setCityCode(request.getCityCode() != null ? request.getCityCode().toLowerCase().trim() : null);
        clientEntity.setPhoneNumber(request.getPhoneNumber() != null ? request.getPhoneNumber().toLowerCase().trim() : null);

        clientEntity = clientRepository.save(clientEntity);
        var resp = clientMapper.entityToDto(clientEntity);

        resp.setInfoByDepartment(clientInfoDepService.saveListClientInfoDep(request, clientEntity, isUpdate));

        return resp;
    }

    private void validateInfoClient(ClientRequest request) {
        if (request.getTaxId() == null || request.getTaxId().isEmpty())
            throw new ClientErrorFormException(simpleMessage("client.required.taxId"));

        if (request.getPaymentTerms() == null )
            throw new ClientErrorFormException(simpleMessage("client.required.payment-terms"));

        if (request.getIndustryId() == null)
            throw new ClientErrorFormException(simpleMessage("client.required.industry"));
    }

    private void validateMinContacts(List<ClientInfoDepRequest> clientInfoDepRequests) {
        boolean validContacts = true;
        for (ClientInfoDepRequest request: clientInfoDepRequests) {
            if (request.getListContacts() != null && !request.getListContacts().isEmpty()) {
                validContacts = false;
                break;
            }
        }

        if (validContacts)
            throw new ClientErrorFormException(simpleMessage("client.min-contacts"));
    }

    private void validateOpenClient(ClientEntity clientEntity, UserEntity userAuthenticated) {
        if (clientEntity.getOpenBy() == null)
            throw new NotOpenClientException(simpleMessage("client.not-block"));
        if ( !clientEntity.getOpenBy().getId().equals(userAuthenticated.getId()))
            throw new NotOpenClientException(compositeMessage("client.not-block-by", new String[]{clientEntity.getOpenBy().getFullName()}));
    }

    private ClientEntity findById(UUID id, boolean validateActive) {
        var client = clientRepository.findById(id).orElseThrow(() ->
                new NotExistClientException(simpleMessage("client.not-exist"))
        );
        if (validateActive && client.getStatus().equals(ClientStatus.INACTIVE))
            throw new NotActiveClientException(simpleMessage("client.not-active"));
        return client;
    }
}
