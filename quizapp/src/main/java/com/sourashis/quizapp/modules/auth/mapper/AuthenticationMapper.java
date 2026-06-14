package com.sourashis.quizapp.modules.auth.mapper;

import com.sourashis.quizapp.modules.auth.dto.AuthenticationRequest;
import com.sourashis.quizapp.modules.auth.dto.AuthenticationResponse;
import com.sourashis.quizapp.modules.auth.entity.User;
import com.sourashis.quizapp.modules.roles.entity.Permission;

import java.util.Set;
import java.util.stream.Collectors;

public class AuthenticationMapper {

    public static User toUserEntity(AuthenticationRequest req) {
        return User.builder()
                .username(req.getUsername())
                .password(req.getPassword())
                .email(req.getEmail())
                .phoneNumber(req.getPhoneNumber())
                .address(req.getAddress())
                .displayName(req.getDisplayName())
                .build();
    }

    public static AuthenticationResponse toUserResponse(User user, String authToken, String refreshToken) {
        String role = user.getRole() != null ? user.getRole().getName() : null;

        Set<String> permissions = user.getRole() != null && user.getRole().getPermissions() != null
                ? user.getRole().getPermissions().stream()
                        .map(Permission::getName)
                        .collect(Collectors.toSet())
                : java.util.Collections.emptySet();

        return AuthenticationResponse.builder()
                .uuid(user.getUuid())
                .username(user.getUsername())
                .role(role)
                .permissions(permissions)
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .displayName(user.getDisplayName())
                .profilePictureUrl(user.getProfilePictureUrl())
                .authToken(authToken)
                .refreshToken(refreshToken)
                .build();
    }
}
