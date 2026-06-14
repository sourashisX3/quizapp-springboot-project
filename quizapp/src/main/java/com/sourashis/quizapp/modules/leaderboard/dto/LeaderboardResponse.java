package com.sourashis.quizapp.modules.leaderboard.dto;

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
    private Long id;
    private String leaderboardType;
    private Long categoryId;
    private String periodStart;
    private String periodEnd;
    private Boolean isActive;
    private int totalEntries;
    private Instant createdAt;
}
