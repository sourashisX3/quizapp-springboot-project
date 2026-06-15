package com.sourashis.quizapp.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Admin view of a user account")
public class AdminUserResponse {

    @Schema(description = "Internal user ID", example = "1")
    private Long id;

    @Schema(description = "UUID of the user", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String uuid;

    @Schema(description = "Username", example = "john_doe")
    private String username;

    @Schema(description = "Email address", example = "john@example.com")
    private String email;

    @Schema(description = "Display name", example = "John Doe")
    private String displayName;

    @Schema(description = "Account status", example = "ACTIVE")
    private String accountStatus;

    @Schema(description = "Whether email is verified", example = "true")
    private Boolean emailVerified;

    @Schema(description = "Role name", example = "ROLE_USER")
    private String role;

    @Schema(description = "Current level", example = "5")
    private Integer level;

    @Schema(description = "Total XP earned", example = "5000")
    private Long totalXp;

    @Schema(description = "Date the user joined", example = "2025-01-15T10:30:00Z")
    private Instant createdAt;

    @Schema(description = "Last login timestamp", example = "2025-06-15T10:30:00Z")
    private Instant lastLoginAt;
}
