package com.sourashis.quizapp.modules.question.entity;

/**
 * Enum representing the difficulty level of a question.
 * Moved to separate file following proper architectural practices.
 */
public enum DifficultyLevel {
    EASY,
    MEDIUM,
    HARD;

    /**
     * Convert string to DifficultyLevel enum.
     *
     * @param value the string value
     * @return DifficultyLevel enum
     * @throws IllegalArgumentException if value doesn't match any enum constant
     */
    public static DifficultyLevel fromString(String value) {
        try {
            return DifficultyLevel.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid difficulty level: " + value + ". Must be one of: EASY, MEDIUM, HARD", e);
        }
    }
}

