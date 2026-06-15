package com.sourashis.quizapp.modules.notification.dto;

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
@Schema(description = "Notification preferences for a user")
public class NotificationPreferenceResponse {

    @Schema(description = "Preference ID", example = "1")
    private Long id;

    @Schema(description = "User ID", example = "1")
    private Long userId;

    @Schema(description = "Notification channel", example = "IN_APP")
    private String channel;

    @Schema(description = "Quiz reminders enabled", example = "true")
    private boolean quizReminders;

    @Schema(description = "Contest updates enabled", example = "true")
    private boolean contestUpdates;

    @Schema(description = "Badge achievement notifications enabled", example = "true")
    private boolean badgeAchievements;

    @Schema(description = "Friend request notifications enabled", example = "true")
    private boolean friendRequests;

    @Schema(description = "Weekly digest enabled", example = "false")
    private boolean weeklyDigest;

    @Schema(description = "Marketing notifications enabled", example = "false")
    private boolean marketing;

    @Schema(description = "Timestamp when created", example = "2025-01-15T10:30:00Z")
    private Instant createdAt;

    @Schema(description = "Timestamp when last updated", example = "2025-01-15T10:30:00Z")
    private Instant updatedAt;
}
