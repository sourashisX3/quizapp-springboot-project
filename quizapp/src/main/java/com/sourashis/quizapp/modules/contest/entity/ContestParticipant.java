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
@Table(name = "contest_participants",
        uniqueConstraints = @UniqueConstraint(columnNames = {"contestId", "userId"}),
        indexes = {
                @Index(columnList = "contestId, score DESC"),
                @Index(columnList = "contestId, positionRank")
        })
public class ContestParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long contestId;

    @Column(nullable = false)
    private Long userId;

    private Long quizAttemptId;

    @Builder.Default
    private Integer score = 0;

    private Integer timeTakenSeconds;

    private Integer positionRank;

    private Double percentile;

    @Builder.Default
    private String status = "REGISTERED";

    private Instant registeredAt;

    private Instant completedAt;

    @PrePersist
    protected void onCreate() {
        registeredAt = Instant.now();
    }
}
