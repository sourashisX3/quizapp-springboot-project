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
@Table(name = "contests", indexes = {
        @Index(columnList = "contestType, startsAt, endsAt"),
        @Index(columnList = "startsAt, endsAt, isActive")
})
public class Contest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String uuid;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String contestType;

    private Long categoryId;

    private String difficulty;

    @Column(nullable = false)
    private Integer numQuestions;

    @Column(nullable = false)
    private Integer timeLimitMinutes;

    @Column(nullable = false)
    private Instant startsAt;

    @Column(nullable = false)
    private Instant endsAt;

    @Builder.Default
    private Integer maxParticipants = 0;

    private Integer minScoreToQualify;

    @Builder.Default
    private Boolean isActive = true;

    @Column(columnDefinition = "TEXT")
    private String rulesJson;

    @Column(columnDefinition = "TEXT")
    private String prizeDescription;

    private Long createdBy;

    @Column(updatable = false)
    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }
}
