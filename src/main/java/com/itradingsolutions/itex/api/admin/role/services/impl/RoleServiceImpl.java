package com.itradingsolutions.itex.api.admin.role.services.impl;

import com.itradingsolutions.itex.api.admin.role.exceptions.NotExistRoleException;
import com.itradingsolutions.itex.api.admin.role.exceptions.NotRoleActiveException;
import com.itradingsolutions.itex.api.admin.role.models.dto.RoleDTO;
import com.itradingsolutions.itex.api.admin.role.models.entities.RoleEntity;
import com.itradingsolutions.itex.api.admin.role.models.mappers.RoleMapper;
import com.itradingsolutions.itex.api.admin.role.repositories.IRoleRepository;
import com.itradingsolutions.itex.api.admin.user.repositories.IUserRepository;
import com.itradingsolutions.itex.api.common.util.services.UtilServiceAbs;
import com.itradingsolutions.itex.api.admin.role.services.IRoleService;
import com.itradingsolutions.itex.config.websocket.WebSocketHandlerItex;
import com.itradingsolutions.itex.config.websocket.WebSocketMessageValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends UtilServiceAbs implements IRoleService {

    private final IRoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final WebSocketHandlerItex socketHandler;
    private final IUserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public RoleEntity findEntityById(UUID id, boolean validateActive) {
         return getRoleById(id, validateActive);
    }

    @Override
    @Transactional
    public RoleDTO create(RoleDTO role) {
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setActive(true);
        return createOrUpdate(role, roleEntity);
    }

    @Override
    @Transactional
    public RoleDTO update(RoleDTO role, UUID roleId) {
        var roleEntity = getRoleById(roleId, true);
        return createOrUpdate(role, roleEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDTO findById(UUID roleId, boolean validateActive) {
        return roleMapper.entityToDto(getRoleById(roleId, validateActive));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDTO> listAll() {
        return roleRepository.fetchAll().stream().map(roleMapper::entityToDto).toList();
    }

    @Override
    @Transactional
    public void disable(UUID roleId) {
        var role = getRoleById(roleId, true);
        role.setActive(false);
        roleRepository.save(role);
        new Thread(() -> {
            var listUsers = userRepository.fetchAllByRoleId(roleId);
            listUsers.forEach(userId -> socketHandler.closeSessionUser(null, userId, WebSocketMessageValue.DISABLE_ROLE));
        }).start();
    }

    @Override
    @Transactional
    public void enable(UUID roleId) {
        var role = getRoleById(roleId, false);
        if (role.isActive()) throw new NotRoleActiveException("The role is active");
        role.setActive(true);
        roleRepository.save(role);
    }

    private RoleDTO createOrUpdate(RoleDTO role, RoleEntity roleEntity) {
        roleEntity.setName(role.getName());
        roleEntity.setDescription(role.getDescription());
        return roleMapper.entityToDto(roleRepository.save(roleEntity));
    }

    private RoleEntity getRoleById(UUID id, boolean validateActive) {
        var role = roleRepository.findById(id).orElseThrow(() ->
                new NotExistRoleException("The consulted role does not exist")
        );

        if (validateActive && !role.isActive())
            throw new NotRoleActiveException("This role is not active");
        return role;
    }
}
