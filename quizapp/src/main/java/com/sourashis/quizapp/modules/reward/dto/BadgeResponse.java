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
public class BadgeResponse {
    private Long id;
    private String name;
    private String description;
    private String iconUrl;
    private String badgeType;
    private Integer pointsReward;
    private Boolean isHidden;
    private Instant createdAt;
    private boolean isAwarded;
    private Instant awardedAt;
}
