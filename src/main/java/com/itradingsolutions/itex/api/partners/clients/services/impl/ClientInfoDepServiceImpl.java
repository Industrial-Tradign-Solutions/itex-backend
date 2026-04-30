package com.itradingsolutions.itex.api.partners.clients.services.impl;

import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleAction;
import com.itradingsolutions.itex.api.admin.role.services.IRoleService;
import com.itradingsolutions.itex.api.admin.user.models.entities.UserEntity;
import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.masters.department.services.IDepartmentService;
import com.itradingsolutions.itex.api.partners.clients.exceptions.NotExistClientDepInfoException;
import com.itradingsolutions.itex.api.partners.clients.models.dto.ClientInfoDepDTO;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientEntity;
import com.itradingsolutions.itex.api.partners.clients.models.entities.ClientInfoDepEntity;
import com.itradingsolutions.itex.api.partners.clients.models.mappers.ClientInfoDepMapper;
import com.itradingsolutions.itex.api.partners.clients.models.requests.ClientRequest;
import com.itradingsolutions.itex.api.partners.clients.repository.IClientInfoDepRepository;
import com.itradingsolutions.itex.api.partners.clients.services.IClientContactService;
import com.itradingsolutions.itex.api.partners.clients.services.IClientInfoDepService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientInfoDepServiceImpl extends UtilServiceAbs implements IClientInfoDepService {

    private final IClientInfoDepRepository clientInfoDepRepository;
    private final IDepartmentService departmentService;
    private final IUserService userService;
    private final ClientInfoDepMapper clientInfoDepMapper;
    private final IClientContactService clientContactService;

    @Override
    @Transactional
    public List<ClientInfoDepDTO> saveListClientInfoDep(ClientRequest request, ClientEntity clientEntity, boolean isUpdate) {
        List<ClientInfoDepDTO> resp = new ArrayList<>();
        request.getInfoByDepartment().forEach(clientInfoDepRequest -> {
            ClientInfoDepEntity clientInfoDepEntity = new ClientInfoDepEntity();

            if (isUpdate) {
                if (clientInfoDepRequest.getId() != null) {
                    clientInfoDepEntity = clientEntity.getInfoByDepartment()
                            .stream()
                            .filter(item -> item.getId().equals(clientInfoDepRequest.getId()))
                            .findFirst().orElseThrow(() ->
                                    new NotExistClientDepInfoException(compositeMessage("client.dep-info.not-exist", new String[]{clientInfoDepRequest.getId().toString()}))
                            );

                } else {
                    clientInfoDepEntity = clientEntity.getInfoByDepartment()
                            .stream()
                            .filter(item -> item.getDepartment().getId().equals(clientInfoDepRequest.getDepartmentId()))
                            .findFirst().orElse(new ClientInfoDepEntity());
                }
            }

            clientInfoDepEntity.setClient(clientEntity);

            if (validChangeTarget(userService.getUserByUsername(getAuthenticatedUsername()))) {
                clientInfoDepEntity.setTarget(clientInfoDepRequest.getTarget().setScale(3, RoundingMode.HALF_DOWN));
            } else {
                if (clientInfoDepEntity.getTarget() == null) {
                    clientInfoDepEntity.setTarget(new BigDecimal(0).setScale(3, RoundingMode.HALF_DOWN));
                }
            }

            clientInfoDepEntity.setNotes(clientInfoDepRequest.getNotes() != null ? normalizeText(clientInfoDepRequest.getNotes()).trim() : null );

            if (clientInfoDepEntity.getDepartment() == null)
                clientInfoDepEntity.setDepartment(departmentService.findEntityById(clientInfoDepRequest.getDepartmentId(), true));

            if (clientInfoDepRequest.getAccountRepId() != null) {
                if (clientInfoDepEntity.getAccountRep() == null
                        || !clientInfoDepEntity.getAccountRep().getId().equals(clientInfoDepRequest.getAccountRepId()))
                    clientInfoDepEntity.setAccountRep(userService.findEntityById(clientInfoDepRequest.getAccountRepId(), true));
            } else {
                clientInfoDepEntity.setAccountRep(null);
            }

            var clientInfoDepDTO = clientInfoDepMapper.entityToDTO(clientInfoDepRepository.save(clientInfoDepEntity));
            clientInfoDepDTO.setListContacts(clientContactService.saveListClientContacts(clientInfoDepEntity, clientInfoDepRequest, isUpdate));

            resp.add(clientInfoDepDTO);
        });
        return resp;
    }

    private boolean validChangeTarget(UserEntity user) {
        if (user.getRole().getId().equals(IRoleService.SUPER_ADMIN_ID))
            return true;

        return user.getRole().getActions().stream().anyMatch(roleAction ->
                roleAction.getAction().getId().equals(ModuleAction.CHANGE_TARGET_CLIENT_INFO.getId())
        );
    }
}
