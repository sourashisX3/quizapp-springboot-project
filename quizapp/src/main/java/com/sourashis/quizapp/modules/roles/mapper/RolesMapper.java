package com.sourashis.quizapp.modules.roles.mapper;

import com.sourashis.quizapp.modules.roles.dto.PermissionResponse;
import com.sourashis.quizapp.modules.roles.dto.RolesResponse;
import com.sourashis.quizapp.modules.roles.entity.Permission;
import com.sourashis.quizapp.modules.roles.entity.Role;

import java.util.Set;
import java.util.stream.Collectors;

public final class RolesMapper {

    private RolesMapper() {}

    public static RolesResponse toRoleResponse(Role role) {
        Set<String> permissionNames = role.getPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());
        return RolesResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .permissions(permissionNames)
                .build();
    }

    public static PermissionResponse toPermissionResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .resource(permission.getResource())
                .action(permission.getAction())
                .description(permission.getDescription())
                .build();
    }
}
