package com.sourashis.quizapp.modules.roles.mapper;

import com.sourashis.quizapp.modules.roles.entity.Permission;
import com.sourashis.quizapp.modules.roles.entity.Role;
import com.sourashis.quizapp.modules.roles.dto.PermissionResponse;
import com.sourashis.quizapp.modules.roles.dto.RolesResponse;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class RolesMapper {

    private RolesMapper() {
    }

    public static RolesResponse toRoleResponse(Role role) {
        Set<String> permNames = role.getPermissions() != null
                ? role.getPermissions().stream().map(Permission::getName).collect(Collectors.toSet())
                : Collections.emptySet();

        return RolesResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .permissions(permNames)
                .build();
    }

    public static PermissionResponse toPermissionResponse(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .build();
    }
}
