package com.sourashis.quizapp.modules.notification.service;

import com.sourashis.quizapp.modules.notification.dto.NotificationResponse;
import com.sourashis.quizapp.modules.notification.entity.Notification;
import com.sourashis.quizapp.modules.notification.entity.NotificationDelivery;
import com.sourashis.quizapp.modules.notification.repository.NotificationDeliveryRepository;
import com.sourashis.quizapp.modules.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationDeliveryRepository notificationDeliveryRepository;

    public NotificationResponse sendToUser(Long userId, String type, String title, String body, String priority) {
        Notification notification = Notification.builder()
                .uuid(UUID.randomUUID().toString())
                .type(type)
                .title(title)
                .body(body)
                .priority(priority != null ? priority : "NORMAL")
                .build();
        notification = notificationRepository.save(notification);

        NotificationDelivery delivery = NotificationDelivery.builder()
                .notificationId(notification.getId())
                .userId(userId)
                .channel("IN_APP")
                .status("SENT")
                .sentAt(Instant.now())
                .build();
        notificationDeliveryRepository.save(delivery);

        return toResponse(notification, delivery);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getUserNotifications(Long userId, int page, int size) {
        List<NotificationDelivery> deliveries = notificationDeliveryRepository
                .findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size));

        return deliveries.stream().map(d -> {
            Notification notification = notificationRepository.findById(d.getNotificationId()).orElse(null);
            if (notification == null) return null;
            return toResponse(notification, d);
        }).filter(java.util.Objects::nonNull).toList();
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationDeliveryRepository.countByUserIdAndStatus(userId, "SENT");
    }

    public void markAsRead(Long userId, Long deliveryId) {
        notificationDeliveryRepository.findById(deliveryId).ifPresent(d -> {
            d.setStatus("READ");
            d.setReadAt(Instant.now());
            notificationDeliveryRepository.save(d);
        });
    }

    public void markAllAsRead(Long userId) {
        notificationDeliveryRepository.markAllAsRead(userId, Instant.now());
    }

    private NotificationResponse toResponse(Notification n, NotificationDelivery d) {
        return NotificationResponse.builder()
                .id(n.getId())
                .uuid(n.getUuid())
                .type(n.getType())
                .title(n.getTitle())
                .body(n.getBody())
                .priority(n.getPriority())
                .status(d.getStatus())
                .channel(d.getChannel())
                .readAt(d.getReadAt())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
