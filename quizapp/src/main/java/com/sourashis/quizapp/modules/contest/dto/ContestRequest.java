package com.sourashis.quizapp.modules.contest.dto;

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
public class ContestRequest {
    @Schema(description = "Title of the contest", example = "Weekly Java Championship")
    private String title;

    @Schema(description = "Detailed description of the contest", example = "Compete against other Java developers in this timed quiz challenge")
    private String description;

    @Schema(description = "Type of contest (TIMED, SCHEDULED)", example = "TIMED")
    private String contestType;

    @Schema(description = "ID of the question category", example = "1")
    private Long categoryId;

    @Schema(description = "Difficulty level (EASY, MEDIUM, HARD, EXPERT)", example = "MEDIUM")
    private String difficulty;

    @Schema(description = "Number of questions in the contest", example = "20")
    private Integer numQuestions;

    @Schema(description = "Time limit in minutes", example = "30")
    private Integer timeLimitMinutes;

    @Schema(description = "Scheduled start time (ISO-8601)", example = "2026-07-01T10:00:00Z")
    private Instant startsAt;

    @Schema(description = "Scheduled end time (ISO-8601)", example = "2026-07-01T11:00:00Z")
    private Instant endsAt;

    @Schema(description = "Maximum number of participants allowed", example = "100")
    private Integer maxParticipants;

    @Schema(description = "Minimum score percentage to qualify for prizes", example = "50")
    private Integer minScoreToQualify;

    @Schema(description = "JSON string containing contest rules", example = "{\"entryFee\": 0, \"maxAttempts\": 1, \"tieBreaker\": \"time\"}")
    private String rulesJson;

    @Schema(description = "Description of the prize for winners", example = "1000 XP + Gold Badge")
    private String prizeDescription;
}
