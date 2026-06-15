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
@Schema(description = "Admin dashboard response with platform-wide statistics")
public class AdminDashboardResponse {
    @Schema(description = "Total number of registered users", example = "1500")
    private long totalUsers;
    @Schema(description = "Total number of quizzes created", example = "200")
    private long totalQuizzes;
    @Schema(description = "Total number of questions in the system", example = "5000")
    private long totalQuestions;
    @Schema(description = "Total number of contests held", example = "50")
    private long totalContests;
    @Schema(description = "Number of currently active contests", example = "5")
    private long activeContests;
    @Schema(description = "Number of users registered today", example = "12")
    private long newUsersToday;
    @Schema(description = "List of most recent user registrations")
    private List<RecentRegistration> recentRegistrations;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Details of a recent user registration")
    public static class RecentRegistration {
        @Schema(description = "Username of the new user", example = "new_user")
        private String username;
        @Schema(description = "Email of the new user", example = "new@example.com")
        private String email;
        @Schema(description = "Role assigned to the new user", example = "ROLE_USER")
        private String role;
        @Schema(description = "Registration timestamp", example = "2025-01-15T10:30:00Z")
        private String createdAt;
    }
}
