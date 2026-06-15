package com.sourashis.quizapp.modules.auth.controller;

import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.modules.auth.entity.User;
import com.sourashis.quizapp.modules.auth.service.FriendshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Friends", description = "Friend management endpoints")
@RestController
@RequestMapping("/api/v1/friends")
public class FriendshipController {

    @Autowired
    private FriendshipService friendshipService;

    @Operation(summary = "Get friends list", description = "Retrieves all accepted friends of the authenticated user")
    @ApiResponse(responseCode = "200", description = "Friends list retrieved successfully")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<List<Map<String, Object>>>> getFriends(@AuthenticationPrincipal User user) {
        List<Map<String, Object>> friends = friendshipService.getFriends(user.getId());
        return ApiResponseWrapper.success(friends, "Friends list retrieved successfully");
    }

    @Operation(summary = "Send friend request", description = "Sends a friend request to another user")
    @ApiResponse(responseCode = "200", description = "Friend request sent successfully")
    @PostMapping("/request/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<Void>> sendRequest(
            @AuthenticationPrincipal User user,
            @PathVariable @Parameter(description = "ID of the user to send request to") Long userId) {
        friendshipService.sendRequest(user.getId(), userId);
        return ApiResponseWrapper.success(null, "Friend request sent successfully");
    }

    @Operation(summary = "Accept friend request", description = "Accepts a pending friend request")
    @ApiResponse(responseCode = "200", description = "Friend request accepted successfully")
    @PostMapping("/accept/{friendshipId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<Void>> acceptRequest(
            @AuthenticationPrincipal User user,
            @PathVariable @Parameter(description = "ID of the friendship to accept") Long friendshipId) {
        friendshipService.acceptRequest(user.getId(), friendshipId);
        return ApiResponseWrapper.success(null, "Friend request accepted successfully");
    }

    @Operation(summary = "Reject friend request", description = "Rejects a pending friend request")
    @ApiResponse(responseCode = "200", description = "Friend request rejected successfully")
    @PostMapping("/reject/{friendshipId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<Void>> rejectRequest(
            @AuthenticationPrincipal User user,
            @PathVariable @Parameter(description = "ID of the friendship to reject") Long friendshipId) {
        friendshipService.rejectRequest(user.getId(), friendshipId);
        return ApiResponseWrapper.success(null, "Friend request rejected successfully");
    }

    @Operation(summary = "Remove friend", description = "Removes a friend or cancels a sent request")
    @ApiResponse(responseCode = "200", description = "Friend removed successfully")
    @DeleteMapping("/{friendshipId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<Void>> removeFriend(
            @AuthenticationPrincipal User user,
            @PathVariable @Parameter(description = "ID of the friendship to remove") Long friendshipId) {
        friendshipService.removeFriend(user.getId(), friendshipId);
        return ApiResponseWrapper.success(null, "Friend removed successfully");
    }

    @Operation(summary = "Get pending requests", description = "Retrieves all incoming pending friend requests for the authenticated user")
    @ApiResponse(responseCode = "200", description = "Pending requests retrieved successfully")
    @GetMapping("/requests")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<List<Map<String, Object>>>> getPendingRequests(@AuthenticationPrincipal User user) {
        List<Map<String, Object>> requests = friendshipService.getPendingRequests(user.getId());
        return ApiResponseWrapper.success(requests, "Pending requests retrieved successfully");
    }

    @Operation(summary = "Get sent requests", description = "Retrieves all outgoing pending friend requests sent by the authenticated user")
    @ApiResponse(responseCode = "200", description = "Sent requests retrieved successfully")
    @GetMapping("/sent")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<List<Map<String, Object>>>> getSentRequests(@AuthenticationPrincipal User user) {
        List<Map<String, Object>> requests = friendshipService.getSentRequests(user.getId());
        return ApiResponseWrapper.success(requests, "Sent requests retrieved successfully");
    }
}
