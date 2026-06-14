package com.sourashis.quizapp.modules.reward.entity;

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
@Table(name = "rewards")
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String rewardType;

    @Column(nullable = false)
    private String sourceType;

    private Long sourceId;

    @Column(length = 500)
    private String rewardValue;

    @Builder.Default
    private Integer xpAmount = 0;

    @Builder.Default
    private Boolean claimed = false;

    private Instant claimedAt;

    private Instant expiresAt;

    @Column(updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
