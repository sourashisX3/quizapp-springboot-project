package com.sourashis.quizapp.modules.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionResponse {

    @Schema(description = "Question ID", example = "1")
    private Long id;
    @Schema(description = "The question text", example = "What is the main advantage of using Spring Boot?")
    private String questionTitle;
    @Schema(description = "List of answer options")
    private List<OptionResponse> options;
    @Schema(description = "Question difficulty", example = "MEDIUM")
    private String difficulty;
    @Schema(description = "Points awarded for correct answer", example = "10")
    private Integer points;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionResponse {
        @Schema(description = "Option ID", example = "1")
        private Long id;
        @Schema(description = "Option text", example = "Auto-configuration")
        private String optionText;
        @Schema(description = "Display order of the option", example = "1")
        private Integer sortOrder;
    }
}
