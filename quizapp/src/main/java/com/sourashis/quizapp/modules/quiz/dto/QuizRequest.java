package com.sourashis.quizapp.modules.quiz.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuizRequest {

    @NotNull
    private Long categoryId;

    @Min(1)
    private Integer numQuestions;

    @NotBlank
    private String title;

    private String difficulty;

    private Integer timeLimitMinutes;

    private Double passingScorePct;
}
