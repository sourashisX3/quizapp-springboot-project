package com.sourashis.quizapp.modules.auth.mapper;

import com.sourashis.quizapp.modules.auth.dto.AuthenticationRequest;
import com.sourashis.quizapp.modules.auth.entity.User;

public class AuthenticationMapper {

    private AuthenticationMapper() {}

    public static User toUserEntity(AuthenticationRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        return user;
    }

    public static User toAdminEntity(AuthenticationRequest request) {
        User admin = new User();
        admin.setUsername(request.getUsername());
        admin.setPassword(request.getPassword());
        return admin;
    }

}
