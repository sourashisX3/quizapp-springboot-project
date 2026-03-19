package com.sourashis.quizapp.modules.quiz.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuizScoreResponse {

    private Integer quizId;
    private String quizTitle;
    private int totalQuestions;
    private int correctAnswers;
    private int wrongAnswers;
    private double scorePercentage;
}

