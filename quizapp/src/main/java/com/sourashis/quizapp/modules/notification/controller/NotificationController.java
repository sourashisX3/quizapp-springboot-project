package com.sourashis.quizapp.modules.notification.controller;

import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.modules.auth.entity.User;
import com.sourashis.quizapp.modules.notification.dto.NotificationResponse;
import com.sourashis.quizapp.modules.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponseWrapper<List<NotificationResponse>>> getNotifications(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<NotificationResponse> notifications = notificationService.getUserNotifications(user.getId(), page, size);
        return ApiResponseWrapper.success(notifications, "Notifications retrieved successfully");
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponseWrapper<Long>> getUnreadCount(@AuthenticationPrincipal User user) {
        long count = notificationService.getUnreadCount(user.getId());
        return ApiResponseWrapper.success(count, "Unread count retrieved");
    }

    @PostMapping("/{deliveryId}/read")
    public ResponseEntity<ApiResponseWrapper<Void>> markAsRead(
            @AuthenticationPrincipal User user,
            @PathVariable Long deliveryId) {
        notificationService.markAsRead(user.getId(), deliveryId);
        return ApiResponseWrapper.success(null, "Notification marked as read");
    }

    @PostMapping("/read-all")
    public ResponseEntity<ApiResponseWrapper<Void>> markAllAsRead(@AuthenticationPrincipal User user) {
        notificationService.markAllAsRead(user.getId());
        return ApiResponseWrapper.success(null, "All notifications marked as read");
    }
}
