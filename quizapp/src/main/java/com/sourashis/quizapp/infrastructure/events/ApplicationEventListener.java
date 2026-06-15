package com.sourashis.quizapp.infrastructure.events;

import com.sourashis.quizapp.modules.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ApplicationEventListener {

    @Autowired
    private NotificationService notificationService;

    @Async
    @EventListener
    public void handleQuizCompleted(QuizCompletedEvent event) {
        notificationService.sendToUser(
                event.userId(),
                "QUIZ_COMPLETED",
                "Quiz Completed",
                "You scored " + event.score() + " points on the quiz",
                "NORMAL"
        );
    }

    @Async
    @EventListener
    public void handleBadgeAwarded(BadgeAwardedEvent event) {
        notificationService.sendToUser(
                event.getUserId(),
                "BADGE_AWARDED",
                "Badge Earned",
                "Congratulations! You earned the " + event.getBadgeName() + " badge!",
                "HIGH"
        );
    }

    @Async
    @EventListener
    public void handleAchievementUnlocked(AchievementUnlockedEvent event) {
        notificationService.sendToUser(
                event.getUserId(),
                "ACHIEVEMENT_UNLOCKED",
                "Achievement Unlocked",
                "You unlocked the " + event.getAchievementName() + " achievement!",
                "HIGH"
        );
    }

    @Async
    @EventListener
    public void handleContestEvent(ContestEvent event) {
        String title;
        String body;

        switch (event.getEventType()) {
            case "JOINED":
                title = "Contest Joined";
                body = "You have successfully joined " + event.getContestTitle();
                break;
            case "STARTED":
                title = "Contest Started";
                body = "The contest " + event.getContestTitle() + " has started!";
                break;
            case "COMPLETED":
                title = "Contest Completed";
                body = "You have completed " + event.getContestTitle();
                break;
            case "WON":
                title = "Contest Won";
                body = "Congratulations! You won " + event.getContestTitle() + "!";
                break;
            default:
                title = "Contest Update";
                body = "Update on " + event.getContestTitle();
        }

        notificationService.sendToUser(event.getUserId(), "CONTEST_" + event.getEventType(), title, body, "NORMAL");
    }
}
