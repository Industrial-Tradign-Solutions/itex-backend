package com.itradingsolutions.itex.api.admin.role.services.impl;

import com.itradingsolutions.itex.api.admin.role.exceptions.NotExistRoleException;
import com.itradingsolutions.itex.api.admin.role.exceptions.RoleMenuEmptyListException;
import com.itradingsolutions.itex.api.admin.role.exceptions.RoleMenuInvalidException;
import com.itradingsolutions.itex.api.admin.role.models.dto.MainMenuDTO;
import com.itradingsolutions.itex.api.admin.role.models.dto.MenuItemDTO;
import com.itradingsolutions.itex.api.admin.role.models.dto.RoleMenuDTO;
import com.itradingsolutions.itex.api.admin.role.models.entities.MenuEntity;
import com.itradingsolutions.itex.api.admin.role.models.entities.RoleEntity;
import com.itradingsolutions.itex.api.admin.role.models.entities.RoleMenuEntity;
import com.itradingsolutions.itex.api.admin.role.models.entities.ids.RoleMenuId;
import com.itradingsolutions.itex.api.admin.role.models.enums.ModuleOption;
import com.itradingsolutions.itex.api.admin.role.models.mappers.MenuMapper;
import com.itradingsolutions.itex.api.admin.role.models.mappers.RoleMenuMapper;
import com.itradingsolutions.itex.api.admin.role.models.requests.RoleMenuRequest;
import com.itradingsolutions.itex.api.admin.role.models.responses.RoleMenuIdsResponse;
import com.itradingsolutions.itex.api.admin.role.models.responses.RoleMenuListResponse;
import com.itradingsolutions.itex.api.admin.role.repositories.IMenuRepository;
import com.itradingsolutions.itex.api.admin.role.repositories.IRoleMenuRepository;
import com.itradingsolutions.itex.api.admin.role.repositories.IRoleRepository;
import com.itradingsolutions.itex.api.admin.role.services.IRoleActionService;
import com.itradingsolutions.itex.api.admin.role.services.IRoleMenuService;
import com.itradingsolutions.itex.api.admin.role.services.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleMenuServiceImpl implements IRoleMenuService {

    private final IMenuRepository menuRepository;
    private final IRoleRepository roleRepository;
    private final MenuMapper menuMapper;
    private final IRoleMenuRepository roleMenuRepository;
    private final RoleMenuMapper roleMenuMapper;
    private final IRoleActionService roleActionService;

    @Override
    @Transactional(readOnly = true)
    public List<MainMenuDTO> listMenuByRoleId(UUID roleId) {
        var listMaiMenus = menuRepository.fetchAll().stream().map(menuMapper::menuEntityToMainMenu).toList();
        List<MenuItemDTO> assignedMenus = null;
        if (IRoleService.SUPER_ADMIN_ID.equals(roleId)) {
            assignedMenus = menuRepository.fetchAllItems().stream().map(menuMapper::menuEntityToMenuItem).toList();
        } else {
            assignedMenus = getAllMenusByRole(roleId).assignedMenus();
        }
        List<MainMenuDTO> resp = new ArrayList<>();
        for (MainMenuDTO menu: listMaiMenus) {
            List<MenuItemDTO> finalAssignedMenus = assignedMenus;
            List<MenuItemDTO> items = new ArrayList<>(menu.getItems().stream().filter(menuItem -> finalAssignedMenus.stream().map(MenuItemDTO::getId).toList().contains(menuItem.getId())).toList());
            items.sort(Comparator.comparingInt(MenuItemDTO::getPosition));
            if (!items.isEmpty()) {
                menu.setItems(items);
                resp.add(menu);
            }
        }
        return resp;
    }

    @Override
    @Transactional(readOnly = true)
    public RoleMenuListResponse findAllMenusByRole(UUID roleId) {
        return getAllMenusByRole(roleId);
    }

    RoleMenuListResponse getAllMenusByRole(UUID roleId) {
        var roleMenus = findRoleMenusByRoleId(roleId);
        List<MenuItemDTO> assigned = new ArrayList<>();
        if (!roleMenus.isEmpty()){
            assigned.addAll(roleMenus.stream().map(RoleMenuDTO::getMenu).toList());
        }
        var allMenus = findAllMenus();
        List<MenuItemDTO> unassigned = allMenus.stream().filter(menuItem -> !assigned.stream().map(MenuItemDTO::getId).toList().contains(menuItem.getId())).toList();
        return new RoleMenuListResponse(unassigned, assigned);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleMenuIdsResponse findAllMenuIds(UUID roleId) {
        if (IRoleService.SUPER_ADMIN_ID.equals(roleId)) {
            var allMenus = findAllMenus();
            List<Long> menuIds= new ArrayList<>(
                    allMenus.stream().map(MenuItemDTO::getId).toList()
            );
            return new RoleMenuIdsResponse(menuIds);
        } else {
            var roleMenus = findRoleMenusByRoleId(roleId);
            if (roleMenus.isEmpty()){
                return new RoleMenuIdsResponse(null);
            }
            List<Long> menuIds= new ArrayList<>(
                    roleMenus.stream().map(roleMenu -> roleMenu.getMenu().getId()).toList()
            );
            return new RoleMenuIdsResponse(menuIds);
        }
    }

    @Override
    @Transactional
    public List<RoleMenuDTO> updateRoleMenus(UUID idRole, RoleMenuRequest roleMenuRequest) {
        RoleEntity roleEntity = findRole(idRole);
        var menuIds = new HashSet<>( roleMenuRequest.getMenuIds() ); //Distinct Ids.
        if (menuIds.isEmpty()){
            deleteMenus(roleEntity);
            roleActionService.deleteActionsWithMenu(roleEntity.getId());
            return Collections.emptyList();
        }
        verifyMainMenus(menuIds);
        List<MenuEntity> menuEntities = menuRepository.findAllById(menuIds);
        if (menuEntities.stream().anyMatch(menuEntity -> !menuEntity.isActive())){
            throw new RoleMenuEmptyListException("There are one or more inactive modules in the list.");
        }
        var menuIdsNotFound = menuIds.stream()
                .filter(id -> !menuEntities.stream().map(MenuEntity::getId).toList().contains(id)).toList();
        if(!menuIdsNotFound.isEmpty()){
            throw new RoleMenuInvalidException("No IDS associated modules found");
        }

        //Synchronize database with delete action
        deleteMenus(roleEntity);
        roleMenuRepository.flush();

        //Create new RoleMenuEntity and persist it with menu Ids from the request
        List<RoleMenuEntity> roleMenuEntities = new ArrayList<>();
        menuEntities.forEach(menuEntity -> {
            RoleMenuEntity roleMenuEntity = new RoleMenuEntity();
            roleMenuEntity.setId(new RoleMenuId(roleEntity.getId(), menuEntity.getId()));
            roleMenuEntity.setRole(roleEntity);
            roleMenuEntity.setMenu(menuEntity);
            roleMenuEntities.add(roleMenuEntity);
        });
        var roleMenusSaved = saveMenus(roleMenuEntities);
        var menuSavedIds = roleMenusSaved.stream()
                .filter(Objects::nonNull)
                .map(roleMenu -> roleMenu.getMenu().getId()).toList();
        roleActionService.deleteActionsWhenMenuDelete(roleEntity.getId(), menuSavedIds);
        return roleMenusSaved;
    }

    private List<RoleMenuDTO> findRoleMenusByRoleId(UUID roleId){
        if (!roleRepository.existsByIdAndActiveIsTrue(roleId)){
            throw new NotExistRoleException("This role nos exist");
        }
        var roleMenuEntities = roleMenuRepository.findAllByRole_IdAndMenu_ActiveIsTrue(roleId);
        return roleMenuMapper.roleMenuEntitiesToRoleMenus(roleMenuEntities);
    }
    private List<MenuItemDTO> findAllMenus(){
        var menuEntities = menuRepository.findAllByActiveIsTrueAndMainOptionIsFalse();
        return roleMenuMapper.menuEntitiesToMenuItems(menuEntities);
    }

    private RoleEntity findRole(UUID roleId){
        Optional<RoleEntity> roleEntity = roleRepository.findById(roleId);
        if (roleEntity.isPresent() && roleEntity.get().isActive()){
            return roleEntity.get();
        } else {
            throw new NotExistRoleException("This Role not Exist");
        }
    }

    private void verifyMainMenus(HashSet<Long> menuIds){
        var menuIdsNotMain = Arrays.stream(ModuleOption.values()).map(ModuleOption::getId).toList();
        if(menuIds.stream().anyMatch(menuId -> !menuIdsNotMain.contains(menuId))){
            throw new RoleMenuInvalidException("This menu not exist.");
        }
    }

    private void deleteMenus(RoleEntity roleEntity){
        roleMenuRepository.deleteAllByRole(roleEntity);
    }

    private List<RoleMenuDTO> saveMenus(List<RoleMenuEntity> roleMenuEntities){
        return roleMenuMapper.roleMenuEntitiesToRoleMenus(
                roleMenuRepository.saveAll(roleMenuEntities)
        );
    }
}
