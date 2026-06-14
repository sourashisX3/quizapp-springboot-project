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
public class ContestRequest {
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
    private String rulesJson;
    private String prizeDescription;
}
