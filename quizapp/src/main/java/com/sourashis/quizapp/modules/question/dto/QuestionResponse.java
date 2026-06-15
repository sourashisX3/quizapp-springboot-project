package com.sourashis.quizapp.modules.question.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {

    @Schema(description = "Question ID", example = "1")
    private Long id;

    @Schema(description = "Question UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String uuid;

    @Schema(description = "Question text", example = "What is the output of System.out.println(2 + 2)?")
    private String title;

    @Schema(description = "Category ID", example = "1")
    private Long categoryId;

    @Schema(description = "Category name", example = "Java")
    private String categoryName;

    @Schema(description = "Difficulty level", example = "EASY")
    private String difficulty;

    @Schema(description = "Question type (MCQ, TRUE_FALSE, SHORT_ANSWER)", example = "MCQ")
    private String questionType;

    @Schema(description = "Time limit in seconds", example = "30")
    private Integer timeLimitSeconds;

    @Schema(description = "Points for correct answer", example = "10")
    private Integer points;

    @Schema(description = "Comma-separated tags", example = "java, basics")
    private String tags;

    @Schema(description = "Answer explanation", example = "2 + 2 is 4")
    private String explanation;

    @Schema(description = "Whether the question is active", example = "true")
    private boolean isActive;

    @Schema(description = "List of answer options")
    private List<OptionResponse> options;

    @Schema(description = "Creation timestamp", example = "2026-01-15T10:30:00Z")
    private Instant createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionResponse {

        @Schema(description = "Option ID", example = "1")
        private Long id;

        @Schema(description = "Option text", example = "4")
        private String optionText;

        @Schema(description = "Display order index", example = "1")
        private Integer sortOrder;

        @Schema(description = "Option-specific explanation", example = "Correct answer")
        private String explanation;
    }
}
