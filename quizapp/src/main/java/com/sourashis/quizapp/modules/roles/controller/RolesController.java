package com.sourashis.quizapp.modules.roles.controller;

import com.sourashis.quizapp.core.audit.Auditable;
import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.modules.roles.dto.PermissionResponse;
import com.sourashis.quizapp.modules.roles.dto.RoleRequest;
import com.sourashis.quizapp.modules.roles.dto.RolesResponse;
import com.sourashis.quizapp.modules.roles.service.RolesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Tag(name = "Roles", description = "Role and permission management endpoints")
@RestController
@RequestMapping("/api/v1/roles")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class RolesController {

    @Autowired
    private RolesService rolesService;

    @Operation(summary = "Get all roles", description = "Retrieves all roles in the system, each with its associated permissions. SUPER_ADMIN only.")
    @ApiResponse(responseCode = "200", description = "Roles retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Forbidden — SUPER_ADMIN role required")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Auditable(action = "READ", resourceType = "ROLE")
    @GetMapping
    public ResponseEntity<ApiResponseWrapper<List<RolesResponse>>> getAllRoles() {
        List<RolesResponse> roles = rolesService.getAllRoles();
        return ApiResponseWrapper.success(roles, "Roles retrieved successfully");
    }

    @Operation(summary = "Get role by ID", description = "Retrieves a single role by its ID, including its associated permissions. SUPER_ADMIN only.")
    @ApiResponse(responseCode = "200", description = "Role retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Forbidden — SUPER_ADMIN role required")
    @ApiResponse(responseCode = "404", description = "Role not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Auditable(action = "READ", resourceType = "ROLE")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<RolesResponse>> getRoleById(@PathVariable @Parameter(description = "ID of the role") Long id) {
        RolesResponse role = rolesService.getRoleById(id);
        return ApiResponseWrapper.success(role, "Role retrieved successfully");
    }

    @Operation(summary = "Create a new role", description = "Creates a new role with optional initial permissions. Role names must be unique. SUPER_ADMIN only.")
    @ApiResponse(responseCode = "201", description = "Role created successfully")
    @ApiResponse(responseCode = "400", description = "Validation failed — name is required")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Forbidden — SUPER_ADMIN role required")
    @ApiResponse(responseCode = "409", description = "Role with this name already exists")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Auditable(action = "CREATE", resourceType = "ROLE")
    @PostMapping
    public ResponseEntity<ApiResponseWrapper<RolesResponse>> createRole(@Valid @Parameter(description = "Role details including name and description") @RequestBody RoleRequest request) {
        RolesResponse role = rolesService.createRole(request);
        return ApiResponseWrapper.created(role, "Role created successfully");
    }

    @Operation(summary = "Update a role", description = "Updates a role's name and/or description. SUPER_ADMIN only.")
    @ApiResponse(responseCode = "200", description = "Role updated successfully")
    @ApiResponse(responseCode = "400", description = "Validation failed")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Forbidden — SUPER_ADMIN role required")
    @ApiResponse(responseCode = "404", description = "Role not found")
    @ApiResponse(responseCode = "409", description = "Role name conflicts with an existing role")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Auditable(action = "UPDATE", resourceType = "ROLE")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<RolesResponse>> updateRole(@PathVariable @Parameter(description = "ID of the role") Long id,
                                                                         @Valid @Parameter(description = "Updated role details") @RequestBody RoleRequest request) {
        RolesResponse role = rolesService.updateRole(id, request);
        return ApiResponseWrapper.success(role, "Role updated successfully");
    }

    @Operation(summary = "Delete a role", description = "Deletes a role. Roles that are assigned to users cannot be deleted. SUPER_ADMIN only.")
    @ApiResponse(responseCode = "200", description = "Role deleted successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Forbidden — SUPER_ADMIN role required")
    @ApiResponse(responseCode = "404", description = "Role not found")
    @ApiResponse(responseCode = "409", description = "Role is assigned to users and cannot be deleted")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Auditable(action = "DELETE", resourceType = "ROLE")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<Void>> deleteRole(@PathVariable @Parameter(description = "ID of the role") Long id) {
        rolesService.deleteRole(id);
        return ApiResponseWrapper.success(null, "Role deleted successfully");
    }

    @Operation(summary = "Add permissions to a role", description = "Adds one or more permissions to an existing role. SUPER_ADMIN only.")
    @ApiResponse(responseCode = "200", description = "Permissions added successfully")
    @ApiResponse(responseCode = "400", description = "Invalid permission names")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Forbidden — SUPER_ADMIN role required")
    @ApiResponse(responseCode = "404", description = "Role not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Auditable(action = "UPDATE", resourceType = "ROLE")
    @PostMapping("/{roleId}/permissions")
    public ResponseEntity<ApiResponseWrapper<RolesResponse>> addPermissions(@PathVariable @Parameter(description = "ID of the role") Long roleId,
                                                                             @RequestBody @Parameter(description = "Set of permission names to add") Set<String> permissionNames) {
        RolesResponse role = rolesService.addPermissionsToRole(roleId, permissionNames);
        return ApiResponseWrapper.success(role, "Permissions added successfully");
    }

    @Operation(summary = "Remove permissions from a role", description = "Removes one or more permissions from an existing role. SUPER_ADMIN only.")
    @ApiResponse(responseCode = "200", description = "Permissions removed successfully")
    @ApiResponse(responseCode = "400", description = "Invalid permission names")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Forbidden — SUPER_ADMIN role required")
    @ApiResponse(responseCode = "404", description = "Role not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Auditable(action = "UPDATE", resourceType = "ROLE")
    @DeleteMapping("/{roleId}/permissions")
    public ResponseEntity<ApiResponseWrapper<RolesResponse>> removePermissions(@PathVariable @Parameter(description = "ID of the role") Long roleId,
                                                                                @RequestBody @Parameter(description = "Set of permission names to remove") Set<String> permissionNames) {
        RolesResponse role = rolesService.removePermissionsFromRole(roleId, permissionNames);
        return ApiResponseWrapper.success(role, "Permissions removed successfully");
    }

    @Operation(summary = "Get all permissions", description = "Retrieves all available permissions in the system. SUPER_ADMIN only.")
    @ApiResponse(responseCode = "200", description = "Permissions retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Forbidden — SUPER_ADMIN role required")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Auditable(action = "READ", resourceType = "ROLE")
    @GetMapping("/permissions")
    public ResponseEntity<ApiResponseWrapper<List<PermissionResponse>>> getAllPermissions() {
        List<PermissionResponse> permissions = rolesService.getAllPermissions();
        return ApiResponseWrapper.success(permissions, "Permissions retrieved successfully");
    }
}
