package com.sourashis.quizapp.modules.mission.entity;

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
@Table(name = "user_missions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "missionId", "missionType"}))
public class UserMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long missionId;

    @Column(nullable = false)
    private String missionType;

    @Builder.Default
    private Integer progress = 0;

    @Column(nullable = false)
    private Integer targetValue;

    @Builder.Default
    private Boolean isCompleted = false;

    private Instant completedAt;

    @Builder.Default
    private Boolean xpRewarded = false;

    @Column(updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
