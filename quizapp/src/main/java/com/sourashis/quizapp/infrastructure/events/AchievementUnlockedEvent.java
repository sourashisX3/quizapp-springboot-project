package com.sourashis.quizapp.infrastructure.events;

public class AchievementUnlockedEvent {

    private final Long userId;
    private final Long achievementId;
    private final String achievementName;
    private final int xpReward;

    public AchievementUnlockedEvent(Long userId, Long achievementId, String achievementName, int xpReward) {
        this.userId = userId;
        this.achievementId = achievementId;
        this.achievementName = achievementName;
        this.xpReward = xpReward;
    }

    public Long getUserId() { return userId; }
    public Long getAchievementId() { return achievementId; }
    public String getAchievementName() { return achievementName; }
    public int getXpReward() { return xpReward; }
}
