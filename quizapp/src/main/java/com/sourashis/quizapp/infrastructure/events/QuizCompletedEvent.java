package com.sourashis.quizapp.infrastructure.events;

import java.time.Instant;

public record QuizCompletedEvent(
        Long userId,
        Long quizId,
        Long attemptId,
        int score,
        int maxScore,
        double scorePct,
        boolean passed,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String eventType() {
        return "quiz.completed";
    }
}
