package com.sourashis.quizapp.infrastructure.events;

public class BadgeAwardedEvent {

    private final Long userId;
    private final Long badgeId;
    private final String badgeName;
    private final int pointsReward;

    public BadgeAwardedEvent(Long userId, Long badgeId, String badgeName, int pointsReward) {
        this.userId = userId;
        this.badgeId = badgeId;
        this.badgeName = badgeName;
        this.pointsReward = pointsReward;
    }

    public Long getUserId() { return userId; }
    public Long getBadgeId() { return badgeId; }
    public String getBadgeName() { return badgeName; }
    public int getPointsReward() { return pointsReward; }
}
