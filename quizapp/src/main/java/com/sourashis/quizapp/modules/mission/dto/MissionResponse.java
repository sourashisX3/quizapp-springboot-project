package com.sourashis.quizapp.modules.mission.dto;

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
@Schema(description = "Response containing a mission with user progress")
public class MissionResponse {

    @Schema(description = "Mission ID", example = "1")
    private Long id;

    @Schema(description = "Mission title", example = "Complete 5 Quizzes")
    private String title;

    @Schema(description = "Mission description", example = "Take and complete 5 quizzes today")
    private String description;

    @Schema(description = "Icon URL", example = "https://example.com/icons/mission.png")
    private String iconUrl;

    @Schema(description = "Mission type", example = "QUIZZES_TAKEN")
    private String missionType;

    @Schema(description = "Target value to complete", example = "5")
    private Integer targetValue;

    @Schema(description = "XP reward on completion", example = "100")
    private Integer xpReward;

    @Schema(description = "Current progress", example = "3")
    private Integer progress;

    @Schema(description = "Whether the mission is completed", example = "false")
    private Boolean isCompleted;

    @Schema(description = "When the mission was completed", example = "2025-06-15T10:30:00Z")
    private Instant completedAt;

    @Schema(description = "DAILY or WEEKLY", example = "DAILY")
    private String missionCategory;
}
