package com.sourashis.quizapp.modules.quiz.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuizRequest {

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Number of questions is required")
    @Min(value = 5, message = "At least 5 question is required")
    private Integer numQuestions;

    @NotBlank(message = "Title is required")
    private String title;
}

