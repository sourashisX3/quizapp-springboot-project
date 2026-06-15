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
@Schema(description = "Public profile response visible to other users")
public class PublicProfileResponse {

    @Schema(description = "Internal user ID", example = "1")
    private Long id;

    @Schema(description = "Username", example = "john_doe")
    private String username;

    @Schema(description = "Display name", example = "John Doe")
    private String displayName;

    @Schema(description = "Short biography", example = "Quiz enthusiast and lifelong learner")
    private String bio;

    @Schema(description = "URL to profile picture", example = "https://example.com/uploads/avatar.jpg")
    private String profilePictureUrl;

    @Schema(description = "Current level", example = "5")
    private Integer level;

    @Schema(description = "Current XP", example = "750")
    private Long currentXp;

    @Schema(description = "XP needed for next level", example = "150")
    private Long xpForNextLevel;

    @Schema(description = "Total XP earned", example = "5000")
    private Long totalXp;

    @Schema(description = "Number of badges earned", example = "3")
    private Integer badgesCount;

    @Schema(description = "Number of achievements unlocked", example = "5")
    private Integer achievementsCount;

    @Schema(description = "Global rank", example = "42")
    private Integer rankGlobal;

    @Schema(description = "Total quizzes passed", example = "35")
    private Integer totalQuizzesPassed;

    @Schema(description = "Total quizzes taken", example = "42")
    private Integer totalQuizzesTaken;

    @Schema(description = "Average score percentage", example = "83.5")
    private Double averageScorePct;

    @Schema(description = "Date the user joined", example = "2025-01-15T10:30:00Z")
    private Instant joinedDate;
}
