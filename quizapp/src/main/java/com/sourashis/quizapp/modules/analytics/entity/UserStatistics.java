package com.sourashis.quizapp.modules.analytics.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_statistics")
public class UserStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Builder.Default
    private Integer totalQuizzesTaken = 0;

    @Builder.Default
    private Integer totalQuizzesPassed = 0;

    @Builder.Default
    private Integer totalQuestionsAnswered = 0;

    @Builder.Default
    private Integer totalCorrectAnswers = 0;

    @Builder.Default
    private Long totalScore = 0L;

    @Builder.Default
    private Long totalXp = 0L;

    @Builder.Default
    private Integer currentStreak = 0;

    @Builder.Default
    private Integer longestStreak = 0;

    @Builder.Default
    private Integer totalContestsParticipated = 0;

    @Builder.Default
    private Integer totalContestsWon = 0;

    private Double averageScorePct;

    private Double averageTimePerQuestionSec;

    private Integer rankGlobal;

    private Integer rankMonthly;

    @Builder.Default
    private Integer badgesCount = 0;

    @Builder.Default
    private Integer achievementsCount = 0;

    private Instant lastActiveAt;

    private Instant lastQuizAt;

    @Column(updatable = false)
    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }
}
