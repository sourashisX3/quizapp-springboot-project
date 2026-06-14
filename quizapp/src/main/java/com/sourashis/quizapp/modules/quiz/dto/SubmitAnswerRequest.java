package com.sourashis.quizapp.modules.quiz.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitAnswerRequest {

    @NotNull
    private Long questionId;

    private Long selectedOptionId;

    private String answerText;
}
