package com.sourashis.quizapp.modules.notification.entity;

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
@Table(name = "notification_preferences", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId", "channel"})
})
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    @Builder.Default
    private String channel = "IN_APP";

    @Builder.Default
    private boolean quizReminders = true;

    @Builder.Default
    private boolean contestUpdates = true;

    @Builder.Default
    private boolean badgeAchievements = true;

    @Builder.Default
    private boolean friendRequests = true;

    @Builder.Default
    private boolean weeklyDigest = false;

    @Builder.Default
    private boolean marketing = false;

    @Column(updatable = false)
    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
