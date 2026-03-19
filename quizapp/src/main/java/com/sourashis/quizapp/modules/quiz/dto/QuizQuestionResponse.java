package com.sourashis.quizapp.modules.quiz.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Question shape returned inside a quiz — rightAnswer intentionally excluded.
 */
@Data
@Builder
public class QuizQuestionResponse {

    private Integer id;
    private String questionTitle;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
}

