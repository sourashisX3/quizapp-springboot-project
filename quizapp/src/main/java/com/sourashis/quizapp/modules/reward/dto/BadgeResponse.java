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
public class BadgeResponse {
    @Schema(description = "Badge ID", example = "1")
    private Long id;

    @Schema(description = "Badge name", example = "Gold Master")
    private String name;

    @Schema(description = "Badge description", example = "Score 90% or higher on 10 quizzes")
    private String description;

    @Schema(description = "Badge icon URL", example = "https://example.com/badges/gold-master.png")
    private String iconUrl;

    @Schema(description = "Badge type (BRONZE, SILVER, GOLD, PLATINUM)", example = "GOLD")
    private String badgeType;

    @Schema(description = "XP points rewarded when earned", example = "500")
    private Integer pointsReward;

    @Schema(description = "Whether the badge is hidden until earned", example = "false")
    private Boolean isHidden;

    @Schema(description = "Badge creation date", example = "2026-01-01T00:00:00Z")
    private Instant createdAt;

    @Schema(description = "Whether the current user has earned this badge", example = "true")
    private boolean isAwarded;

    @Schema(description = "When the badge was awarded (null if not earned)", example = "2026-06-15T10:30:00Z")
    private Instant awardedAt;
}
