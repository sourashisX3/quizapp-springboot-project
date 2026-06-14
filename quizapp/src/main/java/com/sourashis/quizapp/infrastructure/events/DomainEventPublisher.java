package com.sourashis.quizapp.infrastructure.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class DomainEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(DomainEventPublisher.class);

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void publish(DomainEvent event) {
        log.debug("Publishing domain event: {} at {}", event.eventType(), event.occurredAt());
        applicationEventPublisher.publishEvent(event);
    }
}
