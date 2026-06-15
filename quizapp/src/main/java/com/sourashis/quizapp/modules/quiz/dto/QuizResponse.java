package com.sourashis.quizapp.modules.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponse {

    @Schema(description = "Internal ID of the quiz", example = "1")
    private Long id;
    @Schema(description = "UUID of the quiz", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String uuid;
    @Schema(description = "Title of the quiz", example = "Java Fundamentals Quiz")
    private String title;
    @Schema(description = "Detailed description of the quiz", example = "Test your knowledge of core Java concepts including OOP, collections, and exception handling")
    private String description;
    @Schema(description = "ID of the associated category", example = "1")
    private Long categoryId;
    @Schema(description = "Name of the associated category", example = "Java")
    private String categoryName;
    @Schema(description = "Difficulty level", example = "MEDIUM")
    private String difficulty;
    @Schema(description = "Time limit in minutes", example = "15")
    private Integer timeLimitMinutes;
    @Schema(description = "Total number of questions", example = "10")
    private Integer totalQuestions;
    @Schema(description = "Total points available", example = "100")
    private Integer totalPoints;
    @Schema(description = "List of questions in the quiz")
    private List<QuizQuestionResponse> questions;
}
