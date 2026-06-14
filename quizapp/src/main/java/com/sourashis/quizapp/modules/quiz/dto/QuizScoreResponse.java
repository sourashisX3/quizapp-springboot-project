package com.sourashis.quizapp.modules.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizScoreResponse {

    private Long quizId;
    private String quizTitle;
    private Long attemptId;
    private int totalQuestions;
    private int correctAnswers;
    private int wrongAnswers;
    private double scorePercentage;
    private boolean passed;
}
