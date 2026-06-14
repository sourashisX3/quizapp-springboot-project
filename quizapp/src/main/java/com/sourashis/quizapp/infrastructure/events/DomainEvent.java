package com.sourashis.quizapp.infrastructure.events;

import java.time.Instant;

public interface DomainEvent {
    Instant occurredAt();
    String eventType();
}
