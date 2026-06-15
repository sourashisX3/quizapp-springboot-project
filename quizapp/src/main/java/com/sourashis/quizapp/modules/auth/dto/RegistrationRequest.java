package com.sourashis.quizapp.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request body for new user registration")
public class RegistrationRequest {

    @NotBlank(groups = OnRegister.class)
    @Schema(description = "Desired unique username", example = "john_doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(groups = OnRegister.class)
    @Schema(description = "Password (min 6 characters)", example = "P@ssw0rd123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @NotBlank(groups = OnRegister.class)
    @Schema(description = "Valid email address", example = "john@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "Optional phone number", example = "+1234567890")
    private String phoneNumber;

    @Schema(description = "Optional physical address", example = "123 Main St, City")
    private String address;

    @Schema(description = "Optional display name", example = "John Doe")
    private String displayName;

    @Schema(description = "Optional role ID to assign (defaults to ROLE_USER)", example = "2")
    private Long roleId;
}
