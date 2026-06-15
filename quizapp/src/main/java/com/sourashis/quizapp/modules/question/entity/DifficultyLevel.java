package com.sourashis.quizapp.modules.question.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Difficulty level for questions and quizzes", enumAsRef = true)
public enum DifficultyLevel {
    @Schema(description = "Basic level questions suitable for beginners")
    EASY,
    @Schema(description = "Intermediate level questions with moderate complexity")
    MEDIUM,
    @Schema(description = "Advanced level questions requiring deep knowledge")
    HARD,
    @Schema(description = "Expert level questions for highly skilled users")
    EXPERT;

    public static DifficultyLevel fromString(String value) {
        try { return DifficultyLevel.valueOf(value.toUpperCase()); }
        catch (IllegalArgumentException e) { throw new IllegalArgumentException("Invalid difficulty: " + value + ". Must be: EASY, MEDIUM, HARD, EXPERT"); }
    }
}
