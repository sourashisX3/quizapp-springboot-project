package com.sourashis.quizapp.modules.auth.mapper;

import com.sourashis.quizapp.modules.auth.dto.AuthenticationRequest;
import com.sourashis.quizapp.modules.auth.dto.AuthenticationResponse;
import com.sourashis.quizapp.modules.auth.entity.User;
import com.sourashis.quizapp.modules.roles.entity.Permission;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class AuthenticationMapper {

    private AuthenticationMapper() {
    }

    public static User toUserEntity(AuthenticationRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        return user;
    }

    public static AuthenticationResponse toUserResponse(User user) {
        String roleName = user.getRole() != null ? user.getRole().getName() : null;

        Set<String> permissions = user.getRole() != null && user.getRole().getPermissions() != null
                ? user.getRole().getPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toSet())
                : Collections.emptySet();

        return AuthenticationResponse.builder()
                .username(user.getUsername())
                .role(roleName)
                .permissions(permissions)
                .address(user.getAddress())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .authToken(user.getAuthToken())
                .refreshToken(user.getRefreshToken())
                .build();
    }
}
