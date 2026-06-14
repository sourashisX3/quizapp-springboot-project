package com.sourashis.quizapp.modules.quiz.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "quiz_attempts", indexes = {
    @Index(columnList = "userId,quizId"),
    @Index(columnList = "quizId,status"),
    @Index(columnList = "contestParticipantId")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 36)
    private String uuid;

    @Column(nullable = false)
    private Long quizId;

    @Column(nullable = false)
    private Long userId;

    private Long contestParticipantId;

    @Column(nullable = false)
    private Instant startedAt;

    private Instant submittedAt;

    private Integer timeTakenSeconds;

    @Builder.Default
    private Integer score = 0;

    @Column(nullable = false)
    private Integer maxScore;

    private Double scorePct;

    @Column(nullable = false)
    private Boolean passed;

    @Column(nullable = false)
    @Builder.Default
    private String status = "IN_PROGRESS";

    @Column(columnDefinition = "JSON")
    private String answersJson;

    private String deviceInfo;

    private String ipAddress;

    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        createdAt = Instant.now();
        if (startedAt == null) {
            startedAt = Instant.now();
        }
    }
}
