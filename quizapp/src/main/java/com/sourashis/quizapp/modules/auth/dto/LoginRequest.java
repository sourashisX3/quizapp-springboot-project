package com.sourashis.quizapp.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request body for user login")
public class LoginRequest {

    @NotBlank(groups = OnLogin.class)
    @Schema(description = "Username or email for login", example = "john_doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(groups = OnLogin.class)
    @Schema(description = "Account password", example = "P@ssw0rd123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
