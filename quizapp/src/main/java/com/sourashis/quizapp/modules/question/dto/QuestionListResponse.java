package com.sourashis.quizapp.modules.question.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionListResponse {

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

    @Schema(description = "Question type", example = "MCQ")
    private String questionType;

    @Schema(description = "Comma-separated tags", example = "java, basics")
    private String tags;

    @Schema(description = "Creation timestamp", example = "2026-01-15T10:30:00Z")
    private Instant createdAt;
}
