package com.sourashis.quizapp.modules.leaderboard.dto;

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
public class LeaderboardResponse {
    @Schema(description = "Leaderboard ID", example = "1")
    private Long id;
    @Schema(description = "Type of leaderboard (GLOBAL, DAILY, WEEKLY, MONTHLY)", example = "WEEKLY")
    private String leaderboardType;
    @Schema(description = "Category ID this leaderboard is for (null for global)", example = "null")
    private Long categoryId;
    @Schema(description = "Start of the leaderboard period", example = "2026-06-08T00:00:00Z")
    private String periodStart;
    @Schema(description = "End of the leaderboard period", example = "2026-06-14T23:59:59Z")
    private String periodEnd;
    @Schema(description = "Whether this leaderboard is currently active", example = "true")
    private Boolean isActive;
    @Schema(description = "Total number of entries in this leaderboard", example = "500")
    private int totalEntries;
    @Schema(description = "When the leaderboard was generated", example = "2026-06-15T00:00:00Z")
    private Instant createdAt;
}
