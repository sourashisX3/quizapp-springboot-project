package com.sourashis.quizapp.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthenticationRequest {

    @NotBlank(groups = {OnLogin.class, OnRegister.class})
    @Schema(description = "Username for authentication (or registration)", example = "john_doe")
    private String username;

    @NotBlank(groups = {OnLogin.class, OnRegister.class})
    @Schema(description = "Password for authentication (or registration)", example = "P@ssw0rd123")
    private String password;

    @NotBlank(groups = OnRegister.class)
    @Schema(description = "Email address (required for registration)", example = "john@example.com")
    private String email;

    @Schema(description = "Phone number (optional)", example = "+1234567890")
    private String phoneNumber;

    @Schema(description = "Physical address (optional)", example = "123 Main St, City")
    private String address;

    @Schema(description = "Display name (optional)", example = "John Doe")
    private String displayName;

    @Schema(description = "Role name to assign (optional, admin only)", example = "ROLE_USER")
    private String roleName;
}
