package com.sourashis.quizapp.modules.quiz.dto;

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
@Schema(description = "Summary response for quiz listings")
public class QuizSummaryResponse {

    @Schema(description = "Internal ID of the quiz", example = "1")
    private Long id;

    @Schema(description = "UUID of the quiz", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String uuid;

    @Schema(description = "Title of the quiz", example = "Geography Basics")
    private String title;

    @Schema(description = "Description of the quiz", example = "Test your geography knowledge")
    private String description;

    @Schema(description = "ID of the associated category", example = "1")
    private Long categoryId;

    @Schema(description = "Name of the associated category", example = "Geography")
    private String categoryName;

    @Schema(description = "Difficulty level", example = "MEDIUM")
    private String difficulty;

    @Schema(description = "Time limit in minutes", example = "15")
    private Integer timeLimitMinutes;

    @Schema(description = "Total number of questions in the quiz", example = "10")
    private Integer totalQuestions;

    @Schema(description = "Total points available", example = "100")
    private Integer totalPoints;

    @Schema(description = "Maximum number of attempts allowed", example = "3")
    private Integer maxAttempts;

    @Schema(description = "Whether the quiz is published", example = "true")
    private Boolean isPublished;

    @Schema(description = "Timestamp when the quiz was created", example = "2025-01-15T10:30:00Z")
    private Instant createdAt;

    @Schema(description = "Username of the creator", example = "john_doe")
    private String createdByUsername;
}
