package com.sourashis.quizapp.modules.auth.controller;

import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.modules.auth.dto.*;
import com.sourashis.quizapp.modules.auth.entity.User;
import com.sourashis.quizapp.modules.auth.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Profile", description = "User profile management endpoints")
@RestController
@RequestMapping("/api/v1")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Operation(summary = "Get my profile", description = "Retrieves the full profile of the currently authenticated user")
    @ApiResponse(responseCode = "200", description = "Profile retrieved successfully")
    @GetMapping("/users/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<ProfileResponse>> getProfile(@AuthenticationPrincipal User user) {
        ProfileResponse profile = profileService.getProfile(user.getId());
        return ApiResponseWrapper.success(profile, "Profile retrieved successfully");
    }

    @Operation(summary = "Get public profile", description = "Retrieves the public profile of a user by their ID")
    @ApiResponse(responseCode = "200", description = "Public profile retrieved successfully")
    @GetMapping("/profile/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<PublicProfileResponse>> getPublicProfile(
            @PathVariable @Parameter(description = "ID of the user") Long userId) {
        PublicProfileResponse profile = profileService.getPublicProfile(userId);
        return ApiResponseWrapper.success(profile, "Public profile retrieved successfully");
    }

    @Operation(summary = "Update my profile", description = "Updates the profile fields of the currently authenticated user")
    @ApiResponse(responseCode = "200", description = "Profile updated successfully")
    @PutMapping("/users/me/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<ProfileResponse>> updateProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ProfileUpdateRequest request) {
        ProfileResponse profile = profileService.updateProfile(user.getId(), request);
        return ApiResponseWrapper.success(profile, "Profile updated successfully");
    }

    @Operation(summary = "Update my avatar", description = "Updates the profile picture of the currently authenticated user")
    @ApiResponse(responseCode = "200", description = "Avatar updated successfully")
    @PatchMapping("/users/me/avatar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<ProfileResponse>> updateAvatar(
            @AuthenticationPrincipal User user,
            @RequestParam @Parameter(description = "URL of the new avatar image") String avatarUrl) {
        ProfileResponse profile = profileService.updateAvatar(user.getId(), avatarUrl);
        return ApiResponseWrapper.success(profile, "Avatar updated successfully");
    }

    @Operation(summary = "Get my stats", description = "Retrieves comprehensive statistics for the authenticated user")
    @ApiResponse(responseCode = "200", description = "Stats retrieved successfully")
    @GetMapping("/users/me/stats")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<UserStatsResponse>> getStats(@AuthenticationPrincipal User user) {
        UserStatsResponse stats = profileService.getStats(user.getId());
        return ApiResponseWrapper.success(stats, "Stats retrieved successfully");
    }

    @Operation(summary = "Change my password", description = "Changes the password for the authenticated user")
    @ApiResponse(responseCode = "200", description = "Password changed successfully")
    @PutMapping("/users/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<Void>> changePassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PasswordChangeRequest request) {
        profileService.changePassword(user.getId(), request.getOldPassword(), request.getNewPassword());
        return ApiResponseWrapper.success(null, "Password changed successfully");
    }
}
