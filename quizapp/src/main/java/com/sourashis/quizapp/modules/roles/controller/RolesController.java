package com.sourashis.quizapp.modules.roles.controller;

import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.modules.roles.dto.PermissionResponse;
import com.sourashis.quizapp.modules.roles.dto.RoleRequest;
import com.sourashis.quizapp.modules.roles.dto.RolesResponse;
import com.sourashis.quizapp.modules.roles.service.RolesService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/roles")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class RolesController {

    @Autowired
    private RolesService rolesService;

    @GetMapping
    public ResponseEntity<ApiResponseWrapper<List<RolesResponse>>> getAllRoles() {
        List<RolesResponse> roles = rolesService.getAllRoles();
        return ApiResponseWrapper.success(roles, "Roles retrieved successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<RolesResponse>> getRoleById(@PathVariable Long id) {
        RolesResponse role = rolesService.getRoleById(id);
        return ApiResponseWrapper.success(role, "Role retrieved successfully");
    }

    @PostMapping
    public ResponseEntity<ApiResponseWrapper<RolesResponse>> createRole(@Valid @RequestBody RoleRequest request) {
        RolesResponse role = rolesService.createRole(request);
        return ApiResponseWrapper.created(role, "Role created successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<RolesResponse>> updateRole(@PathVariable Long id,
                                                                         @Valid @RequestBody RoleRequest request) {
        RolesResponse role = rolesService.updateRole(id, request);
        return ApiResponseWrapper.success(role, "Role updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<Void>> deleteRole(@PathVariable Long id) {
        rolesService.deleteRole(id);
        return ApiResponseWrapper.success(null, "Role deleted successfully");
    }

    @PostMapping("/{roleId}/permissions")
    public ResponseEntity<ApiResponseWrapper<RolesResponse>> addPermissions(@PathVariable Long roleId,
                                                                             @RequestBody Set<String> permissionNames) {
        RolesResponse role = rolesService.addPermissionsToRole(roleId, permissionNames);
        return ApiResponseWrapper.success(role, "Permissions added successfully");
    }

    @DeleteMapping("/{roleId}/permissions")
    public ResponseEntity<ApiResponseWrapper<RolesResponse>> removePermissions(@PathVariable Long roleId,
                                                                                @RequestBody Set<String> permissionNames) {
        RolesResponse role = rolesService.removePermissionsFromRole(roleId, permissionNames);
        return ApiResponseWrapper.success(role, "Permissions removed successfully");
    }

    @GetMapping("/permissions")
    public ResponseEntity<ApiResponseWrapper<List<PermissionResponse>>> getAllPermissions() {
        List<PermissionResponse> permissions = rolesService.getAllPermissions();
        return ApiResponseWrapper.success(permissions, "Permissions retrieved successfully");
    }
}
