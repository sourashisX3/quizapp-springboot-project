package com.sourashis.quizapp.modules.quiz.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * Request DTO for creating a new Quiz.
 * Accepts only categoryId (not Category entity) following proper DTO architectural patterns.
 * All fields are required and validated before quiz creation.
 */
@Data
public class QuizRequest {

    @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be a positive number")
    private Integer categoryId;

    @NotNull(message = "Number of questions is required")
    @Min(value = 5, message = "At least 5 questions are required")
    private Integer numQuestions;

    @NotBlank(message = "Quiz title is required")
    private String title;
}

