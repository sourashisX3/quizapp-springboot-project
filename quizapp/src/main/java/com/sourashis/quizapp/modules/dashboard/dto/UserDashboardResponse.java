package com.sourashis.quizapp.modules.dashboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User dashboard response with personal statistics and recent activity")
public class UserDashboardResponse {
    @Schema(description = "Username of the user", example = "john_doe")
    private String username;
    @Schema(description = "Email address of the user", example = "john@example.com")
    private String email;
    @Schema(description = "Display name on the profile", example = "John Doe")
    private String displayName;
    @Schema(description = "Role assigned to the user", example = "ROLE_USER")
    private String role;
    @Schema(description = "Total quizzes taken by the user", example = "42")
    private int totalQuizzesTaken;
    @Schema(description = "Total quizzes passed", example = "35")
    private int totalQuizzesPassed;
    @Schema(description = "Total contests participated in", example = "15")
    private int totalContestsParticipated;
    @Schema(description = "Total contests won", example = "5")
    private int totalContestsWon;
    @Schema(description = "Total XP earned", example = "12000")
    private long totalXp;
    @Schema(description = "Current login/quiz streak in days", example = "7")
    private int currentStreak;
    @Schema(description = "Number of badges earned", example = "8")
    private int badgesCount;
    @Schema(description = "Number of achievements unlocked", example = "12")
    private int achievementsCount;
    @Schema(description = "Average score percentage across all quizzes", example = "85.5")
    private Double averageScorePct;
    @Schema(description = "Number of unread notifications", example = "3")
    private int unreadNotifications;
    @Schema(description = "List of recent quiz attempts")
    private List<RecentAttempt> recentAttempts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Summary of a recent quiz attempt")
    public static class RecentAttempt {
        @Schema(description = "ID of the quiz attempted", example = "1")
        private Long quizId;
        @Schema(description = "Score achieved in the attempt", example = "80")
        private int score;
        @Schema(description = "Maximum possible score", example = "100")
        private int maxScore;
        @Schema(description = "Score as a percentage", example = "80.0")
        private Double scorePct;
        @Schema(description = "Whether the attempt was passed", example = "true")
        private boolean passed;
        @Schema(description = "Status of the attempt", example = "COMPLETED")
        private String status;
    }
}
