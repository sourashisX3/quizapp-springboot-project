package com.sourashis.quizapp.modules.question.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class QuestionRequest {

    @NotBlank
    private String title;

    @NotNull
    private Long categoryId;

    @NotBlank
    private String difficulty;

    private String questionType;

    private Integer timeLimitSeconds;

    private Integer points = 10;

    private String tags;

    private String explanation;

    @NotEmpty
    private List<OptionRequest> options;

    @Data
    public static class OptionRequest {

        @NotBlank
        private String optionText;

        private boolean isCorrect;

        private String explanation;
    }
}
