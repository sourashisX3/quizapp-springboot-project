package com.sourashis.quizapp.infrastructure.events;

public class ContestEvent {

    private final Long userId;
    private final Long contestId;
    private final String contestTitle;
    private final String eventType;

    public ContestEvent(Long userId, Long contestId, String contestTitle, String eventType) {
        this.userId = userId;
        this.contestId = contestId;
        this.contestTitle = contestTitle;
        this.eventType = eventType;
    }

    public Long getUserId() { return userId; }
    public Long getContestId() { return contestId; }
    public String getContestTitle() { return contestTitle; }
    public String getEventType() { return eventType; }
}
