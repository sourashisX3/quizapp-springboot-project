package com.sourashis.quizapp.modules.leaderboard.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntryResponse {
    @Schema(description = "User ID", example = "1")
    private Long userId;
    @Schema(description = "Username", example = "john_doe")
    private String username;
    @Schema(description = "Display name", example = "John Doe")
    private String displayName;
    @Schema(description = "Total score / XP", example = "8500")
    private Long score;
    @Schema(description = "Current rank position (1 = first)", example = "1")
    private int rank;
    @Schema(description = "Additional metadata as JSON string", example = "{\"quizzesTaken\": 42, \"accuracy\": 83.5}")
    private String metadataJson;
}
