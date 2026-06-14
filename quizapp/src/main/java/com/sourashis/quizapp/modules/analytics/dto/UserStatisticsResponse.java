package com.sourashis.quizapp.modules.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatisticsResponse {
    private Long userId;
    private int totalQuizzesTaken;
    private int totalQuizzesPassed;
    private int totalQuestionsAnswered;
    private int totalCorrectAnswers;
    private long totalScore;
    private long totalXp;
    private int currentStreak;
    private int longestStreak;
    private int totalContestsParticipated;
    private int totalContestsWon;
    private Double averageScorePct;
    private int badgesCount;
    private int achievementsCount;
}
