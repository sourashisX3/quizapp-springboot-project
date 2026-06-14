package com.sourashis.quizapp.modules.contest.entity;

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
@Table(name = "contest_leaderboard")
public class ContestLeaderboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long contestId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer score;

    private Integer timeTakenSeconds;

    @Column(nullable = false)
    private Integer positionRank;

    private Double percentile;

    private Instant lastUpdated;

    @PrePersist
    protected void onCreate() {
        lastUpdated = Instant.now();
    }
}
