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
@Schema(description = "Full profile response for the authenticated user")
public class ProfileResponse {

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

    @Schema(description = "Short biography", example = "Quiz enthusiast and lifelong learner")
    private String bio;

    @Schema(description = "URL to profile picture", example = "https://example.com/uploads/avatar.jpg")
    private String profilePictureUrl;

    @Schema(description = "JSON string containing social media links", example = "{\"github\":\"https://github.com/johndoe\"}")
    private String socialLinksJson;

    @Schema(description = "Phone number", example = "+1234567890")
    private String phoneNumber;

    @Schema(description = "Account status", example = "ACTIVE")
    private String accountStatus;

    @Schema(description = "Whether email is verified", example = "true")
    private Boolean emailVerified;

    @Schema(description = "Current level", example = "5")
    private Integer level;

    @Schema(description = "Current XP", example = "750")
    private Long currentXp;

    @Schema(description = "XP needed for next level", example = "150")
    private Long xpForNextLevel;

    @Schema(description = "Role name", example = "ROLE_USER")
    private String role;

    @Schema(description = "Current login streak in days", example = "7")
    private Integer currentStreak;

    @Schema(description = "Longest login streak in days", example = "30")
    private Integer longestStreak;

    @Schema(description = "Total XP earned", example = "5000")
    private Long totalXp;

    @Schema(description = "Total quizzes taken", example = "42")
    private Integer totalQuizzesTaken;

    @Schema(description = "Total quizzes passed", example = "35")
    private Integer totalQuizzesPassed;

    @Schema(description = "Number of badges earned", example = "3")
    private Integer badgesCount;

    @Schema(description = "Number of achievements unlocked", example = "5")
    private Integer achievementsCount;

    @Schema(description = "Global rank", example = "42")
    private Integer rankGlobal;

    @Schema(description = "Date the user joined", example = "2025-01-15T10:30:00Z")
    private Instant joinedDate;
}
