package com.sourashis.quizapp.modules.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationResponse {

    private String username;
    private String role;
    private String email;
    private String phoneNumber;
    private String address;
    private String profilePicture;
    private String refreshToken;
    private String authToken;
}
