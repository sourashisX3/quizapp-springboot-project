package com.sourashis.quizapp.modules.quiz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponse {

    private Long id;
    private String uuid;
    private String title;
    private String description;
    private Long categoryId;
    private String categoryName;
    private String difficulty;
    private Integer timeLimitMinutes;
    private Integer totalQuestions;
    private Integer totalPoints;
    private List<QuizQuestionResponse> questions;
}
