package com.sourashis.quizapp.modules.roles.service;

import com.sourashis.quizapp.modules.roles.dto.PermissionResponse;
import com.sourashis.quizapp.modules.roles.dto.RoleRequest;
import com.sourashis.quizapp.modules.roles.dto.RolesResponse;
import com.sourashis.quizapp.modules.roles.entity.Permission;
import com.sourashis.quizapp.modules.roles.entity.Role;
import com.sourashis.quizapp.modules.roles.exception.PermissionNotFoundException;
import com.sourashis.quizapp.modules.roles.exception.RoleAlreadyExistsException;
import com.sourashis.quizapp.modules.roles.exception.RoleNotFoundException;
import com.sourashis.quizapp.modules.roles.mapper.RolesMapper;
import com.sourashis.quizapp.modules.roles.repository.PermissionRepository;
import com.sourashis.quizapp.modules.roles.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RolesService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Transactional(readOnly = true)
    public List<RolesResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(RolesMapper::toRoleResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RolesResponse getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException(id.intValue()));
        return RolesMapper.toRoleResponse(role);
    }

    @Transactional
    public RolesResponse createRole(RoleRequest request) {
        if (roleRepository.existsByName(request.getName())) {
            throw new RoleAlreadyExistsException(request.getName());
        }
        Set<Permission> permissions = request.getPermissionNames() != null
                ? resolvePermissions(request.getPermissionNames())
                : Set.of();
        Role role = Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .permissions(permissions)
                .build();
        role = roleRepository.save(role);
        return RolesMapper.toRoleResponse(role);
    }

    @Transactional
    public RolesResponse updateRole(Long id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException(id.intValue()));
        if (request.getName() != null) {
            role.setName(request.getName());
        }
        if (request.getDescription() != null) {
            role.setDescription(request.getDescription());
        }
        if (request.getPermissionNames() != null) {
            role.setPermissions(resolvePermissions(request.getPermissionNames()));
        }
        role = roleRepository.save(role);
        return RolesMapper.toRoleResponse(role);
    }

    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException(id.intValue()));
        roleRepository.delete(role);
    }

    @Transactional
    public RolesResponse addPermissionsToRole(Long roleId, Set<String> permissionNames) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException(roleId.intValue()));
        Set<Permission> permissions = resolvePermissions(permissionNames);
        role.getPermissions().addAll(permissions);
        role = roleRepository.save(role);
        return RolesMapper.toRoleResponse(role);
    }

    @Transactional
    public RolesResponse removePermissionsFromRole(Long roleId, Set<String> permissionNames) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException(roleId.intValue()));
        Set<Permission> permissions = resolvePermissions(permissionNames);
        role.getPermissions().removeAll(permissions);
        role = roleRepository.save(role);
        return RolesMapper.toRoleResponse(role);
    }

    @Transactional(readOnly = true)
    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(RolesMapper::toPermissionResponse)
                .collect(Collectors.toList());
    }

    private Set<Permission> resolvePermissions(Set<String> names) {
        return names.stream()
                .map(name -> permissionRepository.findByName(name)
                        .orElseThrow(() -> new PermissionNotFoundException(name)))
                .collect(Collectors.toSet());
    }
}
