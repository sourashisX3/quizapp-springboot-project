package com.sourashis.quizapp.modules.notification.controller;

import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.modules.auth.entity.User;
import com.sourashis.quizapp.modules.notification.dto.NotificationResponse;
import com.sourashis.quizapp.modules.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Notifications", description = "In-app notification management endpoints")
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Operation(summary = "Get user notifications", description = "Retrieves a paginated list of notifications for the authenticated user. Notifications include contest reminders, badge unlocks, friend requests, and system announcements. Results are sorted by creation date in descending order.")
    @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping
    public ResponseEntity<ApiResponseWrapper<List<NotificationResponse>>> getNotifications(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "Page number (zero-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "20") @RequestParam(defaultValue = "20") int size) {
        List<NotificationResponse> notifications = notificationService.getUserNotifications(user.getId(), page, size);
        return ApiResponseWrapper.success(notifications, "Notifications retrieved successfully");
    }

    @Operation(summary = "Get unread notification count", description = "Returns the total count of unread notifications for the authenticated user. Useful for displaying a badge count on the notification icon in the UI.")
    @ApiResponse(responseCode = "200", description = "Unread count retrieved")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponseWrapper<Long>> getUnreadCount(@Parameter(hidden = true) @AuthenticationPrincipal User user) {
        long count = notificationService.getUnreadCount(user.getId());
        return ApiResponseWrapper.success(count, "Unread count retrieved");
    }

    @Operation(summary = "Mark a single notification as read", description = "Marks a specific notification delivery as read by its delivery ID. Only the notification's recipient can mark it as read. Idempotent — marking an already-read notification has no effect.")
    @ApiResponse(responseCode = "200", description = "Notification marked as read")
    @ApiResponse(responseCode = "400", description = "Invalid delivery ID")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "404", description = "Notification delivery not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PostMapping("/{deliveryId}/read")
    public ResponseEntity<ApiResponseWrapper<Void>> markAsRead(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "ID of the notification delivery to mark as read", required = true) @PathVariable Long deliveryId) {
        notificationService.markAsRead(user.getId(), deliveryId);
        return ApiResponseWrapper.success(null, "Notification marked as read");
    }

    @Operation(summary = "Mark all notifications as read", description = "Marks every unread notification for the authenticated user as read. This operation is irreversible. Useful when the user wants to clear all notification badges at once.")
    @ApiResponse(responseCode = "200", description = "All notifications marked as read")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PostMapping("/read-all")
    public ResponseEntity<ApiResponseWrapper<Void>> markAllAsRead(@Parameter(hidden = true) @AuthenticationPrincipal User user) {
        notificationService.markAllAsRead(user.getId());
        return ApiResponseWrapper.success(null, "All notifications marked as read");
    }
}
