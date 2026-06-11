package com.sourashis.quizapp.modules.roles.service;

import com.sourashis.quizapp.modules.roles.dto.*;
import com.sourashis.quizapp.modules.roles.entity.Permission;
import com.sourashis.quizapp.modules.roles.entity.Role;
import com.sourashis.quizapp.modules.roles.exception.PermissionNotFoundException;
import com.sourashis.quizapp.modules.roles.exception.RoleAlreadyExistsException;
import com.sourashis.quizapp.modules.roles.exception.RoleNotFoundException;
import com.sourashis.quizapp.modules.roles.mapper.RolesMapper;
import com.sourashis.quizapp.modules.roles.repository.PermissionRepository;
import com.sourashis.quizapp.modules.roles.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service layer for Role & Permission management.
 * Handles business logic for CRUD operations on roles and permissions.
 * Validates existence and uniqueness constraints before persisting changes.
 */
@Service
public class RolesService {

    private static final Logger log = LoggerFactory.getLogger(RolesService.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    /**
     * Get all roles with their associated permissions.
     *
     * @return List of RolesResponse DTOs
     */
    public List<RolesResponse> getAllRoles() {
        log.info("Fetching all roles");
        return roleRepository.findAll().stream()
                .map(RolesMapper::toRoleResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific role by its ID.
     *
     * @param id the role ID
     * @return RolesResponse DTO with role details
     * @throws RoleNotFoundException if role not found
     */
    public RolesResponse getRoleById(Integer id) {
        log.info("Fetching role with ID: {}", id);
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Role not found with ID: {}", id);
                    return new RoleNotFoundException(id);
                });
        return RolesMapper.toRoleResponse(role);
    }

    /**
     * Create a new role with the specified permissions.
     * Validates that the role name is unique and all permissions exist.
     *
     * @param request RoleRequest with name, description, and permissionNames
     * @return the created RolesResponse DTO
     * @throws RoleAlreadyExistsException if role name already exists
     * @throws PermissionNotFoundException if a permission is not found
     */
    public RolesResponse createRole(RoleRequest request) {
        log.info("Creating role: {}", request.getName());

        if (roleRepository.findByName(request.getName()).isPresent()) {
            log.warn("Role already exists: {}", request.getName());
            throw new RoleAlreadyExistsException(request.getName());
        }

        Set<Permission> permissions = resolvePermissions(request.getPermissionNames());

        Role role = Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .permissions(permissions)
                .build();

        role = roleRepository.save(role);
        log.info("Role created successfully: {} (ID: {})", role.getName(), role.getId());
        return RolesMapper.toRoleResponse(role);
    }

    /**
     * Update an existing role's name, description, and/or permissions.
     * Only non-null fields from the request are applied.
     *
     * @param id      the role ID to update
     * @param request RoleRequest with updated fields
     * @return the updated RolesResponse DTO
     * @throws RoleNotFoundException if role not found
     * @throws PermissionNotFoundException if a permission is not found
     */
    public RolesResponse updateRole(Integer id, RoleRequest request) {
        log.info("Updating role with ID: {}", id);
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Role not found with ID: {}", id);
                    return new RoleNotFoundException(id);
                });

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
        log.info("Role updated successfully: {} (ID: {})", role.getName(), role.getId());
        return RolesMapper.toRoleResponse(role);
    }

    /**
     * Delete a role by its ID.
     *
     * @param id the role ID to delete
     * @throws RoleNotFoundException if role not found
     */
    public void deleteRole(Integer id) {
        log.info("Deleting role with ID: {}", id);
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Role not found with ID: {}", id);
                    return new RoleNotFoundException(id);
                });
        roleRepository.delete(role);
        log.info("Role deleted successfully: {} (ID: {})", role.getName(), id);
    }

    /**
     * Add permissions to an existing role.
     * Merges the new permissions with any existing ones.
     *
     * @param roleId          the role ID
     * @param permissionNames set of permission names to add
     * @return the updated RolesResponse DTO
     * @throws RoleNotFoundException if role not found
     * @throws PermissionNotFoundException if a permission is not found
     */
    public RolesResponse addPermissionsToRole(Integer roleId, Set<String> permissionNames) {
        log.info("Adding {} permissions to role ID: {}", permissionNames.size(), roleId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> {
                    log.error("Role not found with ID: {}", roleId);
                    return new RoleNotFoundException(roleId);
                });

        Set<Permission> permissions = resolvePermissions(permissionNames);
        if (role.getPermissions() == null) {
            role.setPermissions(new HashSet<>());
        }
        role.getPermissions().addAll(permissions);
        role = roleRepository.save(role);
        log.info("Permissions added to role ID: {}", roleId);
        return RolesMapper.toRoleResponse(role);
    }

    /**
     * Remove permissions from an existing role.
     *
     * @param roleId          the role ID
     * @param permissionNames set of permission names to remove
     * @return the updated RolesResponse DTO
     * @throws RoleNotFoundException if role not found
     * @throws PermissionNotFoundException if a permission is not found
     */
    public RolesResponse removePermissionsFromRole(Integer roleId, Set<String> permissionNames) {
        log.info("Removing {} permissions from role ID: {}", permissionNames.size(), roleId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> {
                    log.error("Role not found with ID: {}", roleId);
                    return new RoleNotFoundException(roleId);
                });

        Set<Permission> permissions = resolvePermissions(permissionNames);
        role.getPermissions().removeAll(permissions);
        role = roleRepository.save(role);
        log.info("Permissions removed from role ID: {}", roleId);
        return RolesMapper.toRoleResponse(role);
    }

    /**
     * Get all available permissions in the system.
     *
     * @return List of PermissionResponse DTOs
     */
    public List<PermissionResponse> getAllPermissions() {
        log.info("Fetching all permissions");
        return permissionRepository.findAll().stream()
                .map(RolesMapper::toPermissionResponse)
                .collect(Collectors.toList());
    }

    /**
     * Resolve a set of permission names to their Permission entities.
     *
     * @param permissionNames set of permission name strings
     * @return Set of Permission entities
     * @throws PermissionNotFoundException if any permission name is not found
     */
    private Set<Permission> resolvePermissions(Set<String> permissionNames) {
        if (permissionNames == null || permissionNames.isEmpty()) {
            return new HashSet<>();
        }
        return permissionNames.stream()
                .map(name -> permissionRepository.findByName(name)
                        .orElseThrow(() -> {
                            log.error("Permission not found: {}", name);
                            return new PermissionNotFoundException(name);
                        }))
                .collect(Collectors.toSet());
    }
}
