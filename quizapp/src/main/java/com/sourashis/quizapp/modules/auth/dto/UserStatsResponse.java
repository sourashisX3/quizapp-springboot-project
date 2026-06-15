package com.sourashis.quizapp.modules.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Comprehensive user statistics response")
public class UserStatsResponse {

    @Schema(description = "Total quizzes taken", example = "100")
    private Integer totalQuizzesTaken;

    @Schema(description = "Total quizzes passed", example = "80")
    private Integer totalQuizzesPassed;

    @Schema(description = "Total questions answered", example = "500")
    private Integer totalQuestionsAnswered;

    @Schema(description = "Total correct answers", example = "400")
    private Integer totalCorrectAnswers;

    @Schema(description = "Accuracy percentage", example = "80.0")
    private Double accuracyPercentage;

    @Schema(description = "Current login streak in days", example = "7")
    private Integer currentStreak;

    @Schema(description = "Longest login streak in days", example = "30")
    private Integer longestStreak;

    @Schema(description = "Total XP earned", example = "5000")
    private Long totalXp;

    @Schema(description = "Total score", example = "40000")
    private Long totalScore;

    @Schema(description = "Total contests participated", example = "10")
    private Integer totalContestsParticipated;

    @Schema(description = "Total contests won", example = "3")
    private Integer totalContestsWon;

    @Schema(description = "Average score percentage", example = "83.5")
    private Double averageScorePct;

    @Schema(description = "Average time per question in seconds", example = "15.2")
    private Double averageTimePerQuestionSec;

    @Schema(description = "Number of badges earned", example = "3")
    private Integer badgesCount;

    @Schema(description = "Number of achievements unlocked", example = "5")
    private Integer achievementsCount;

    @Schema(description = "Global rank", example = "42")
    private Integer rankGlobal;

    @Schema(description = "Monthly rank", example = "15")
    private Integer rankMonthly;

    @Schema(description = "Current level", example = "5")
    private Integer level;

    @Schema(description = "Category-wise performance breakdown")
    private Map<String, Object> categoryPerformance;
}
