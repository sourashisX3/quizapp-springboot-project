package com.sourashis.quizapp.modules.analytics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatisticsResponse {
    @Schema(description = "User ID", example = "1")
    private Long userId;

    @Schema(description = "Total quizzes taken", example = "42")
    private int totalQuizzesTaken;

    @Schema(description = "Total quizzes passed", example = "35")
    private int totalQuizzesPassed;

    @Schema(description = "Total questions answered", example = "420")
    private int totalQuestionsAnswered;

    @Schema(description = "Total correct answers", example = "350")
    private int totalCorrectAnswers;

    @Schema(description = "Cumulative score across all quizzes", example = "35000")
    private long totalScore;

    @Schema(description = "Total XP earned", example = "8500")
    private long totalXp;

    @Schema(description = "Current consecutive day streak", example = "7")
    private int currentStreak;

    @Schema(description = "Longest consecutive day streak", example = "30")
    private int longestStreak;

    @Schema(description = "Total contests participated in", example = "15")
    private int totalContestsParticipated;

    @Schema(description = "Total contests won", example = "3")
    private int totalContestsWon;

    @Schema(description = "Average score percentage", example = "83.33")
    private Double averageScorePct;

    @Schema(description = "Number of badges earned", example = "8")
    private int badgesCount;

    @Schema(description = "Number of achievements unlocked", example = "12")
    private int achievementsCount;
}
