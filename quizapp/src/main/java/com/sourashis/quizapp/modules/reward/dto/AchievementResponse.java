package com.sourashis.quizapp.modules.reward.dto;

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
public class AchievementResponse {
    @Schema(description = "Achievement ID", example = "1")
    private Long id;

    @Schema(description = "Achievement name", example = "First Win")
    private String name;

    @Schema(description = "Achievement description", example = "Complete your first quiz")
    private String description;

    @Schema(description = "Achievement icon URL", example = "https://example.com/achievements/first-win.png")
    private String iconUrl;

    @Schema(description = "Type of criteria to fulfill (QUIZZES_TAKEN, STREAK, TOTAL_XP, ACCURACY)", example = "QUIZZES_TAKEN")
    private String criteriaType;

    @Schema(description = "Target value to complete the achievement", example = "1")
    private Integer criteriaValue;

    @Schema(description = "XP rewarded on completion", example = "100")
    private Integer xpReward;

    @Schema(description = "When the achievement was created", example = "2026-01-01T00:00:00Z")
    private Instant createdAt;

    @Schema(description = "Current progress toward completion", example = "1")
    private int progress;

    @Schema(description = "Whether the achievement has been completed", example = "true")
    private boolean isCompleted;

    @Schema(description = "When the achievement was completed (null if not completed)", example = "2026-06-15T10:30:00Z")
    private Instant completedAt;
}
