package com.sourashis.quizapp.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthenticationRequest {

    @NotBlank(groups = {OnLogin.class, OnRegister.class})
    private String username;

    @NotBlank(groups = {OnLogin.class, OnRegister.class})
    private String password;

    @NotBlank(groups = OnRegister.class)
    private String email;

    private String phoneNumber;

    private String address;

    private String displayName;

    private String roleName;
}
