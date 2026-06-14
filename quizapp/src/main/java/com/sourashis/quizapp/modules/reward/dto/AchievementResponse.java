package com.sourashis.quizapp.modules.reward.dto;

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
    private Long id;
    private String name;
    private String description;
    private String iconUrl;
    private String criteriaType;
    private Integer criteriaValue;
    private Integer xpReward;
    private Instant createdAt;
    private int progress;
    private boolean isCompleted;
    private Instant completedAt;
}
