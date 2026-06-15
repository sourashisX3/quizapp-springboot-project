package com.sourashis.quizapp.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "User UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String uuid;

    @Schema(description = "Username", example = "john_doe")
    private String username;

    @Schema(description = "User role name", example = "ROLE_USER")
    private String role;

    @Schema(description = "Set of granted permission strings", example = "[quiz:read, quiz:attempt]")
    private Set<String> permissions;

    @Schema(description = "Email address", example = "john@example.com")
    private String email;

    @Schema(description = "Phone number", example = "+1234567890")
    private String phoneNumber;

    @Schema(description = "Physical address", example = "123 Main St, City")
    private String address;

    @Schema(description = "Display name", example = "John Doe")
    private String displayName;

    @Schema(description = "Profile picture URL", example = "https://example.com/uploads/avatar.jpg")
    private String profilePictureUrl;

    @Schema(description = "JWT access token for API authorization", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String authToken;

    @Schema(description = "Refresh token for obtaining new access tokens", example = "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4=")
    private String refreshToken;
}
