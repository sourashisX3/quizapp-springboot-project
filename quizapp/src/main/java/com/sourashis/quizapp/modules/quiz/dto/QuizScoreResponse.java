package com.sourashis.quizapp.modules.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizScoreResponse {

    @Schema(description = "ID of the quiz", example = "1")
    private Long quizId;
    @Schema(description = "Title of the quiz", example = "Java Fundamentals Quiz")
    private String quizTitle;
    @Schema(description = "ID of this attempt", example = "1")
    private Long attemptId;
    @Schema(description = "Total number of questions in the quiz", example = "10")
    private int totalQuestions;
    @Schema(description = "Number of correct answers", example = "8")
    private int correctAnswers;
    @Schema(description = "Number of wrong answers", example = "2")
    private int wrongAnswers;
    @Schema(description = "Score as a percentage", example = "80.0")
    private double scorePercentage;
    @Schema(description = "Whether the user passed the quiz", example = "true")
    private boolean passed;
}
