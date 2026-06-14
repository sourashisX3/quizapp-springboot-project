package com.sourashis.quizapp.modules.question.entity;
public enum DifficultyLevel {
    EASY, MEDIUM, HARD, EXPERT;
    public static DifficultyLevel fromString(String value) {
        try { return DifficultyLevel.valueOf(value.toUpperCase()); }
        catch (IllegalArgumentException e) { throw new IllegalArgumentException("Invalid difficulty: " + value + ". Must be: EASY, MEDIUM, HARD, EXPERT"); }
    }
}
