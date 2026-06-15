package com.sourashis.quizapp.modules.question.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class QuestionRequest {

    @NotBlank
    @Schema(description = "Question title/text", example = "What is the output of System.out.println(2 + 2)?")
    private String title;

    @NotNull
    @Schema(description = "ID of the category this question belongs to", example = "1")
    private Long categoryId;

    @NotBlank
    @Schema(description = "Difficulty level (EASY, MEDIUM, HARD, EXPERT)", example = "EASY")
    private String difficulty;

    @Schema(description = "Type of question (MCQ, TRUE_FALSE, SHORT_ANSWER)", example = "MCQ")
    private String questionType;

    @Schema(description = "Time limit to answer in seconds", example = "30")
    private Integer timeLimitSeconds;

    @Schema(description = "Points awarded for correct answer", example = "10")
    private Integer points = 10;

    @Schema(description = "Comma-separated tags for categorization", example = "java, basics, math")
    private String tags;

    @Schema(description = "Explanation shown after answering", example = "2 + 2 equals 4 in Java arithmetic")
    private String explanation;

    @NotEmpty
    @Schema(description = "List of answer options (at least 2, one must be correct)")
    private List<OptionRequest> options;

    @Data
    public static class OptionRequest {

        @NotBlank
        @Schema(description = "Text of this option", example = "4")
        private String optionText;

        @Schema(description = "Whether this option is correct", example = "true")
        private boolean isCorrect;

        @Schema(description = "Explanation specific to this option", example = "Correct! 2 + 2 = 4")
        private String explanation;
    }
}
