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
public class NotificationResponse {
    @Schema(description = "Notification ID", example = "1")
    private Long id;

    @Schema(description = "Notification UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String uuid;

    @Schema(description = "Notification type (CONTEST_REMINDER, BADGE_UNLOCKED, FRIEND_REQUEST, SYSTEM)", example = "BADGE_UNLOCKED")
    private String type;

    @Schema(description = "Notification title", example = "New Badge Unlocked!")
    private String title;

    @Schema(description = "Notification body text", example = "Congratulations! You've earned the 'Gold Master' badge.")
    private String body;

    @Schema(description = "Priority level (HIGH, MEDIUM, LOW)", example = "HIGH")
    private String priority;

    @Schema(description = "Delivery status (SENT, DELIVERED, READ)", example = "SENT")
    private String status;

    @Schema(description = "Delivery channel (IN_APP, EMAIL, PUSH)", example = "IN_APP")
    private String channel;

    @Schema(description = "When the notification was read (null if unread)", example = "2026-06-15T10:30:00Z")
    private Instant readAt;

    @Schema(description = "When the notification was created", example = "2026-06-15T10:00:00Z")
    private Instant createdAt;
}
