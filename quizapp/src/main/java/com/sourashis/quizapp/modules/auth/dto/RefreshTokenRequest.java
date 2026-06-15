package com.sourashis.quizapp.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {

    @NotBlank
    @Schema(description = "The refresh token obtained from login or previous refresh", example = "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4=")
    private String refreshToken;
}
