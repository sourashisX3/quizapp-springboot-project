package com.sourashis.quizapp.modules.question.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Response DTO for Question.
 * Provides a clean separation between the Question entity and what's exposed to clients.
 * Excludes the right answer to prevent exposing correct answers in read operations.
 */
@Data
@Builder
public class QuestionResponse {

    private Integer id;
    private String questionTitle;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String difficultyLevel;
    private Integer categoryId;
    private String categoryName;
}

