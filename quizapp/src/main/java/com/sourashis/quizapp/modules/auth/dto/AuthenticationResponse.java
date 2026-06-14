package com.sourashis.quizapp.modules.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {

    private String uuid;
    private String username;
    private String role;
    private Set<String> permissions;
    private String email;
    private String phoneNumber;
    private String address;
    private String displayName;
    private String profilePictureUrl;
    private String authToken;
    private String refreshToken;
}
