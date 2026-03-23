package com.sourashis.quizapp.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthenticationRequest {

    @NotBlank(message = "Username is required", groups = {OnLogin.class, OnRegister.class})
    private String username;
    @NotBlank(message = "Password is required", groups = {OnLogin.class, OnRegister.class})
    private String password;

    // register only fields
    @NotBlank(message = "Email is required", groups = OnRegister.class)
    private String email;
    @NotBlank(message = "Phone number is required", groups = OnRegister.class)
    private String phoneNumber;
    @NotBlank(message = "Address is required", groups = OnRegister.class)
    private String address;
}

