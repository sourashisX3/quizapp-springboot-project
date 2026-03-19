package com.sourashis.quizapp.modules.quiz.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitAnswerRequest {

    @NotNull(message = "Question id is required")
    private Integer id;

    @NotNull(message = "Response is required")
    private String response;
}

