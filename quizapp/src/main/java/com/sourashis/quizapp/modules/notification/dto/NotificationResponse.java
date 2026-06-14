package com.sourashis.quizapp.modules.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private String uuid;
    private String type;
    private String title;
    private String body;
    private String priority;
    private String status;
    private String channel;
    private Instant readAt;
    private Instant createdAt;
}
