package com.sourashis.quizapp.modules.contest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContestResponse {
    private Long id;
    private String uuid;
    private String title;
    private String description;
    private String contestType;
    private Long categoryId;
    private String difficulty;
    private Integer numQuestions;
    private Integer timeLimitMinutes;
    private Instant startsAt;
    private Instant endsAt;
    private Integer maxParticipants;
    private Integer minScoreToQualify;
    private Boolean isActive;
    private String rulesJson;
    private String prizeDescription;
    private Long createdBy;
    private Instant createdAt;
}
