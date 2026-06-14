package com.sourashis.quizapp.modules.leaderboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntryResponse {
    private Long userId;
    private String username;
    private String displayName;
    private Long score;
    private int rank;
    private String metadataJson;
}
