package com.sourashis.quizapp.modules.contest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Response containing contest details")
public class ContestResponse {
    @Schema(description = "Internal ID of the contest", example = "1")
    private Long id;
    @Schema(description = "UUID of the contest", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String uuid;
    @Schema(description = "Title of the contest", example = "Weekly Quiz Challenge")
    private String title;
    @Schema(description = "Detailed description of the contest", example = "Test your knowledge in this weekly quiz!")
    private String description;
    @Schema(description = "Type of contest", example = "TIMED")
    private String contestType;
    @Schema(description = "ID of the associated category", example = "1")
    private Long categoryId;
    @Schema(description = "Difficulty level", example = "MEDIUM")
    private String difficulty;
    @Schema(description = "Number of questions in the contest", example = "20")
    private Integer numQuestions;
    @Schema(description = "Time limit in minutes", example = "30")
    private Integer timeLimitMinutes;
    @Schema(description = "Scheduled start time", example = "2025-02-01T10:00:00Z")
    private Instant startsAt;
    @Schema(description = "Scheduled end time", example = "2025-02-01T11:00:00Z")
    private Instant endsAt;
    @Schema(description = "Maximum number of participants allowed", example = "100")
    private Integer maxParticipants;
    @Schema(description = "Minimum score percentage to qualify", example = "50")
    private Integer minScoreToQualify;
    @Schema(description = "Whether the contest is currently active", example = "true")
    private Boolean isActive;
    @Schema(description = "JSON string containing contest rules", example = "{\"entryFee\":10,\"maxAttempts\":1}")
    private String rulesJson;
    @Schema(description = "Description of the prize for winners", example = "1000 XP points")
    private String prizeDescription;
    @Schema(description = "ID of the user who created the contest", example = "1")
    private Long createdBy;
    @Schema(description = "Username of the creator", example = "john_doe")
    private String createdByUsername;
    @Schema(description = "Timestamp when the contest was created", example = "2025-01-15T10:30:00Z")
    private Instant createdAt;
    @Schema(description = "Current user's participation status", example = "REGISTERED")
    private String userStatus;
    @Schema(description = "Current user's score", example = "85")
    private Integer userScore;
    @Schema(description = "Current user's rank", example = "3")
    private Integer userRank;
}
