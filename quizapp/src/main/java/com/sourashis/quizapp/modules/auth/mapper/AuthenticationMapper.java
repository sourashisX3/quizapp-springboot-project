package com.sourashis.quizapp.modules.auth.mapper;

import com.sourashis.quizapp.modules.auth.dto.AuthenticationRequest;
import com.sourashis.quizapp.modules.auth.dto.AuthenticationResponse;
import com.sourashis.quizapp.modules.auth.entity.User;

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
        return AuthenticationResponse.builder()
                .username(user.getUsername())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .address(user.getAddress())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .authToken(user.getAuthToken())
                .refreshToken(user.getRefreshToken())
                .build();
    }
}
