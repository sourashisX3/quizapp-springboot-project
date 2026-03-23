package com.sourashis.quizapp.modules.question.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * Request DTO for creating or updating a Question.
 * Validates all required fields and accepts only categoryId (not Category entity).
 * Follows DTO best practices by not importing entity classes.
 */
@Data
public class QuestionRequest {

    @NotBlank(message = "Question title is required")
    private String questionTitle;

    @NotBlank(message = "Option 1 is required")
    private String option1;

    @NotBlank(message = "Option 2 is required")
    private String option2;

    @NotBlank(message = "Option 3 is required")
    private String option3;

    @NotBlank(message = "Option 4 is required")
    private String option4;

    @NotBlank(message = "Right answer is required")
    private String rightAnswer;

    @NotBlank(message = "Difficulty level is required")
    private String difficultyLevel;

    @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be a positive number")
    private Integer categoryId;
}

