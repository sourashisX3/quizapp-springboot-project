package com.sourashis.quizapp.modules.question.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

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

    @NotBlank(message = "Category is required")
    private String category;
}

