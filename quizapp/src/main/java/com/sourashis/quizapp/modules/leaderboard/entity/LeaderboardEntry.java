package com.sourashis.quizapp.modules.leaderboard.entity;

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
@Table(name = "leaderboard_entries",
        uniqueConstraints = @UniqueConstraint(columnNames = {"leaderboardId", "userId"}),
        indexes = @Index(columnList = "leaderboardId, positionRank"))
public class LeaderboardEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long leaderboardId;

    @Column(nullable = false)
    private Long userId;

    @Column(name = "score", nullable = false)
    private Long score;

    @Column(nullable = false)
    private Integer positionRank;

    @Column(columnDefinition = "TEXT")
    private String metadataJson;

    private Instant calculatedAt;

    @PrePersist
    protected void onCreate() {
        calculatedAt = Instant.now();
    }
}
