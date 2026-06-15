package com.sourashis.quizapp.modules.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuizRequest {

    @NotNull
    @Schema(description = "ID of the category this quiz belongs to", example = "1")
    private Long categoryId;

    @Min(1)
    @Schema(description = "Number of questions to include in the quiz", example = "10")
    private Integer numQuestions;

    @NotBlank
    @Schema(description = "Title of the quiz", example = "Java Fundamentals Quiz")
    private String title;

    @Schema(description = "Difficulty level (EASY, MEDIUM, HARD, EXPERT)", example = "MEDIUM")
    private String difficulty;

    @Schema(description = "Time limit for the quiz in minutes", example = "15")
    private Integer timeLimitMinutes;

    @Schema(description = "Minimum score percentage required to pass", example = "60")
    private Double passingScorePct;
}
