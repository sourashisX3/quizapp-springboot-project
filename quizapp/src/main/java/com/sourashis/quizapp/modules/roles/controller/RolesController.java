package com.sourashis.quizapp.modules.roles.controller;

import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.modules.roles.dto.*;
import com.sourashis.quizapp.modules.roles.service.RolesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * REST Controller for Role & Permission management.
 * Provides CRUD operations for roles and permissions.
 * Allows assigning/removing permissions to/from roles at runtime.
 */
@RestController
@RequestMapping("/roles")
public class RolesController {

    @Autowired
    private RolesService rolesService;

    /**
     * GET /roles
     * Returns all roles with their associated permissions.
     * Access: Any authenticated user with 'role:read' permission
     *
     * @return ResponseEntity with list of all roles
     */
    @GetMapping
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<ApiResponseWrapper<List<RolesResponse>>> getAllRoles() {
        return ApiResponseWrapper.success(rolesService.getAllRoles(), "Roles fetched successfully!");
    }

    /**
     * GET /roles/{id}
     * Returns a specific role by its ID with associated permissions.
     * Access: Any authenticated user with 'role:read' permission
     *
     * @param id the role ID
     * @return ResponseEntity with role details
     * @throws RuntimeException if role not found
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<ApiResponseWrapper<RolesResponse>> getRoleById(@PathVariable Integer id) {
        return ApiResponseWrapper.success(rolesService.getRoleById(id), "Role fetched successfully!");
    }

    /**
     * POST /roles
     * Creates a new role with the specified permissions.
     * Access: Any authenticated user with 'role:create' permission
     *
     * @param request RoleRequest with name, description, and permissionNames
     * @return ResponseEntity with created role
     * @throws RuntimeException if role name already exists or permission not found
     */
    @PostMapping
    @PreAuthorize("hasAuthority('role:create')")
    public ResponseEntity<ApiResponseWrapper<RolesResponse>> createRole(
            @Valid @RequestBody RoleRequest request) {
        return ApiResponseWrapper.created(rolesService.createRole(request), "Role created successfully!");
    }

    /**
     * PUT /roles/{id}
     * Updates an existing role's name, description, and/or permissions.
     * Access: Any authenticated user with 'role:update' permission
     *
     * @param id      the role ID to update
     * @param request RoleRequest with updated fields
     * @return ResponseEntity with updated role
     * @throws RuntimeException if role not found or permission not found
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('role:update')")
    public ResponseEntity<ApiResponseWrapper<RolesResponse>> updateRole(
            @PathVariable Integer id,
            @Valid @RequestBody RoleRequest request) {
        return ApiResponseWrapper.success(rolesService.updateRole(id, request), "Role updated successfully!");
    }

    /**
     * DELETE /roles/{id}
     * Deletes a role by its ID.
     * Access: Any authenticated user with 'role:delete' permission
     *
     * @param id the role ID to delete
     * @return ResponseEntity with success message
     * @throws RuntimeException if role not found
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('role:delete')")
    public ResponseEntity<ApiResponseWrapper<Void>> deleteRole(@PathVariable Integer id) {
        rolesService.deleteRole(id);
        return ApiResponseWrapper.success(null, "Role deleted successfully!");
    }

    /**
     * POST /roles/{roleId}/permissions
     * Adds permissions to an existing role.
     * Access: Any authenticated user with 'role:update' permission
     *
     * @param roleId          the role ID to add permissions to
     * @param permissionNames set of permission names to add
     * @return ResponseEntity with updated role
     * @throws RuntimeException if role or permission not found
     */
    @PostMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('role:update')")
    public ResponseEntity<ApiResponseWrapper<RolesResponse>> addPermissions(
            @PathVariable Integer roleId,
            @RequestBody Set<String> permissionNames) {
        return ApiResponseWrapper.success(
                rolesService.addPermissionsToRole(roleId, permissionNames),
                "Permissions added to role successfully!");
    }

    /**
     * DELETE /roles/{roleId}/permissions
     * Removes permissions from an existing role.
     * Access: Any authenticated user with 'role:update' permission
     *
     * @param roleId          the role ID to remove permissions from
     * @param permissionNames set of permission names to remove
     * @return ResponseEntity with updated role
     * @throws RuntimeException if role or permission not found
     */
    @DeleteMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('role:update')")
    public ResponseEntity<ApiResponseWrapper<RolesResponse>> removePermissions(
            @PathVariable Integer roleId,
            @RequestBody Set<String> permissionNames) {
        return ApiResponseWrapper.success(
                rolesService.removePermissionsFromRole(roleId, permissionNames),
                "Permissions removed from role successfully!");
    }

    /**
     * GET /roles/permissions
     * Returns all available permissions in the system.
     * Access: Any authenticated user with 'role:read' permission
     *
     * @return ResponseEntity with list of all permissions
     */
    @GetMapping("/permissions")
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<ApiResponseWrapper<List<PermissionResponse>>> getAllPermissions() {
        return ApiResponseWrapper.success(rolesService.getAllPermissions(), "Permissions fetched successfully!");
    }
}
