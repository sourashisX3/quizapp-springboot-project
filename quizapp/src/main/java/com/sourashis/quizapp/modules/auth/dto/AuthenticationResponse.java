package com.sourashis.quizapp.modules.auth.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class AuthenticationResponse {

    private String username;
    private String role;
    private Set<String> permissions;
    private String email;
    private String phoneNumber;
    private String address;
    private String profilePicture;
    private String refreshToken;
    private String authToken;
}
