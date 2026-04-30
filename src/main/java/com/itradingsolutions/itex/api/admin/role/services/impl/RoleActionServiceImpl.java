package com.itradingsolutions.itex.api.admin.role.services.impl;

import com.itradingsolutions.itex.api.admin.role.exceptions.NotExistRoleException;
import com.itradingsolutions.itex.api.admin.role.exceptions.RoleActionInvalidException;
import com.itradingsolutions.itex.api.admin.role.models.dto.ActionDTO;
import com.itradingsolutions.itex.api.admin.role.models.dto.RoleActionDTO;
import com.itradingsolutions.itex.api.admin.role.models.entities.ActionEntity;
import com.itradingsolutions.itex.api.admin.role.models.entities.RoleActionEntity;
import com.itradingsolutions.itex.api.admin.role.models.entities.RoleEntity;
import com.itradingsolutions.itex.api.admin.role.models.entities.ids.RoleActionId;
import com.itradingsolutions.itex.api.admin.role.models.mappers.RoleActionMapper;
import com.itradingsolutions.itex.api.admin.role.models.requests.RoleActionRequest;
import com.itradingsolutions.itex.api.admin.role.models.responses.RoleActionIdsResponse;
import com.itradingsolutions.itex.api.admin.role.models.responses.RoleActionListResponse;
import com.itradingsolutions.itex.api.admin.role.repositories.IActionRepository;
import com.itradingsolutions.itex.api.admin.role.repositories.IRoleActionRepository;
import com.itradingsolutions.itex.api.admin.role.repositories.IRoleMenuRepository;
import com.itradingsolutions.itex.api.admin.role.repositories.IRoleRepository;
import com.itradingsolutions.itex.api.admin.role.services.IRoleActionService;
import com.itradingsolutions.itex.api.admin.role.services.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class RoleActionServiceImpl implements IRoleActionService {

    private final IRoleActionRepository roleActionRepository;
    private  final IRoleMenuRepository roleMenuRepository;
    private final IActionRepository actionRepository;
    private final IRoleRepository roleRepository;
    private final RoleActionMapper roleActionMapper;

    private static final String ROLE_NOT_FOUND = "This role not exist";

    @Transactional(readOnly = true)
    @Override
    public RoleActionListResponse findAllActionsByRole(UUID roleId) {
        List<ActionDTO> assigned = new ArrayList<>();
        var roleActions = findRoleActionsByRoleId(roleId);
        if (!roleActions.isEmpty()){
            assigned.addAll(roleActions.stream().map(RoleActionDTO::getAction).toList());
        }
        List<Long> roleActionIds = roleActions.stream()
                .map(roleAction -> roleAction.getAction().getId()).toList();
        List<ActionDTO> unassigned = validActions(roleId).stream()
                .filter(action -> !roleActionIds.contains(action.getId())).toList();
        return new RoleActionListResponse(unassigned, assigned);
    }

    @Transactional(readOnly = true)
    @Override
    public RoleActionIdsResponse findAllActionIds(UUID roleId) {

        if (IRoleService.SUPER_ADMIN_ID.equals(roleId)) {
            var allActions = actionRepository.findAll();
            List<Long> actionIds= new ArrayList<>(
                    allActions.stream().map(ActionEntity::getId).toList()
            );
            return new RoleActionIdsResponse(actionIds);
        } else {
            var roleActions = findRoleActionsByRoleId(roleId);
            if(roleActions.isEmpty()){
                return new RoleActionIdsResponse(null);
            }
            List<Long> actionIds= new ArrayList<>(
                    roleActions.stream().map(roleAction -> roleAction.getAction().getId()).toList()
            );
            return new RoleActionIdsResponse(actionIds);
        }
    }

    @Transactional
    @Override
    public List<RoleActionDTO> updateRoleActions(UUID idRole, RoleActionRequest roleActionRequest) {
        RoleEntity roleEntity = roleRepository.findById(idRole).orElseThrow(() ->new NotExistRoleException(ROLE_NOT_FOUND));
        var actionIds = new HashSet<>( roleActionRequest.getActionIds() );
        if (actionIds.isEmpty()){
            deleteActions(roleEntity);
            return Collections.emptyList();
        }
        List<ActionEntity> actionEntities = actionRepository.findAllById(actionIds);
        if (actionEntities.stream().anyMatch(actionEntity -> !actionEntity.isActive())){
            throw new RoleActionInvalidException("There are one or more inactive actions in the list.");
        }
        var actionIdsNotFound = actionIds.stream()
                .filter(id -> !actionEntities.stream().map(ActionEntity::getId).toList().contains(id)).toList();
        if(!actionIdsNotFound.isEmpty()){
            throw new RoleActionInvalidException("No actions associated with IDS were found");
        }
        List<String> messages = new ArrayList<>();
        List<Long> validActionIds = validActions(idRole).stream().map(ActionDTO::getId).toList();
        actionEntities.forEach(actionEntity -> {
            if(!validActionIds.contains(actionEntity.getId())){
                messages.add("The action: " + actionEntity.getName() + ", is not allowed for this role");
            }
        });
        if(!messages.isEmpty()){
            throw new RoleActionInvalidException(String.join(" - ", messages));
        }
        //Synchronize database with delete action
        deleteActions(roleEntity);
        roleActionRepository.flush();

        //Create new RoleActionEntity and persist it with action Ids from the request
        List<RoleActionEntity> roleActionEntities = new ArrayList<>();
        actionEntities.forEach(actionEntity -> {
            RoleActionEntity roleActionEntity = new RoleActionEntity();
            roleActionEntity.setId(new RoleActionId(roleEntity.getId(), actionEntity.getId()));
            roleActionEntity.setRole(roleEntity);
            roleActionEntity.setAction(actionEntity);
            roleActionEntities.add(roleActionEntity);
        });
        return saveActions(roleActionEntities);
    }

    @Transactional
    @Override
    public void deleteActionsWhenMenuDelete(UUID roleId, List<Long> roleMenuIds){
        List<ActionDTO> validActions = validateActions(roleMenuIds);
        List<Long> actionIds = validActions.stream().filter(Objects::nonNull).map(ActionDTO::getId).toList();
        List<RoleActionEntity> roleActions = roleActionRepository.findAllByRole_Id(roleId);
        var actionEntities = roleActions.stream().map(RoleActionEntity::getAction).toList();
        var actions = roleActionMapper.actionEntitiesToActions(actionEntities);
        for(var action : actions){
            if(!actionIds.contains(action.getId())){
                roleActionRepository.deleteByRole_idAndAction_Id(roleId, action.getId());
            }
        }
    }

    @Transactional
    @Override
    public void deleteActionsWithMenu(UUID roleId){
        var roleActions = roleActionRepository.findAllByRole_Id(roleId);
        if(!roleActions.isEmpty()){
            var actions = roleActions.stream().map(RoleActionEntity::getAction).toList();
            var actionsWithMenu = actions.stream().filter(actionEntity -> actionEntity.getMenu() != null).toList();
            if(!actionsWithMenu.isEmpty()){
                actionsWithMenu.forEach(actionEntity -> roleActionRepository.deleteByRole_idAndAction_Id(roleId, actionEntity.getId()));
            }
        }
    }

    private List<RoleActionDTO> saveActions(List<RoleActionEntity> roleActionEntities){
        return roleActionMapper.roleActionEntitiesToRoleAction(
                roleActionRepository.saveAll(roleActionEntities)
        );
    }

    private void deleteActions(RoleEntity roleEntity){
        roleActionRepository.deleteAllByRole(roleEntity);
    }

    private List<Long> findRoleMenuIds(UUID roleId){
        if (!roleRepository.existsByIdAndActiveIsTrue(roleId)){
            throw new NotExistRoleException(ROLE_NOT_FOUND);
        }
        var roleMenuEntities = roleMenuRepository.findAllByRole_IdAndMenu_ActiveIsTrue(roleId);
        if(roleMenuEntities.isEmpty()){
            return Collections.emptyList();
        }
        return roleMenuEntities.stream()
                .map(roleMenu -> roleMenu.getMenu().getId()).toList();
    }

    private List<ActionDTO> validActions(UUID roleId){
        var roleMenuIds = findRoleMenuIds(roleId);
        return validateActions(roleMenuIds);
    }

    private List<ActionDTO> validateActions(List<Long> roleMenuIds){
        if(roleMenuIds.isEmpty()){
            return roleActionMapper.actionEntitiesToActions(actionRepository.findAllByActiveIsTrue())
                    .stream()
                    .filter(action -> action.getMenu() == null)
                    .toList();
        }
        return roleActionMapper.actionEntitiesToActions(actionRepository.findAllByActiveIsTrue())
                .stream()
                .filter(Objects::nonNull)
                .filter(action -> {
                    if (action.getMenu() != null){
                        return roleMenuIds.contains(action.getMenu().getId());
                    } else {
                        return true;
                    }
                })
                .toList();
    }

    private List<RoleActionDTO> findRoleActionsByRoleId(UUID roleId){
        if (!roleRepository.existsByIdAndActiveIsTrue(roleId)){
            throw new NotExistRoleException(ROLE_NOT_FOUND);
        }
        var roleActionEntities = roleActionRepository.findAllByRole_IdAndAction_ActiveIsTrue(roleId);
        return roleActionMapper.roleActionEntitiesToRoleAction(roleActionEntities);
    }
}
