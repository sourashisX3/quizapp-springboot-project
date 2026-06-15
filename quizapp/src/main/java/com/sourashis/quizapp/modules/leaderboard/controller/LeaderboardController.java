package com.sourashis.quizapp.modules.leaderboard.controller;

import com.sourashis.quizapp.core.audit.Auditable;
import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.modules.auth.entity.User;
import com.sourashis.quizapp.modules.leaderboard.dto.LeaderboardEntryResponse;
import com.sourashis.quizapp.modules.leaderboard.dto.LeaderboardResponse;
import com.sourashis.quizapp.modules.leaderboard.service.LeaderboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Leaderboard", description = "Leaderboard rankings and entries endpoints")
@RestController
@RequestMapping("/api/v1/leaderboard")
@PreAuthorize("hasAuthority('leaderboard:read')")
public class LeaderboardController {

    @Autowired
    private LeaderboardService leaderboardService;

    @Operation(summary = "Get leaderboard", description = "Retrieves the leaderboard filtered by type and optionally by category")
    @ApiResponse(responseCode = "200", description = "Leaderboard retrieved successfully")
    @Auditable(action = "READ", resourceType = "LEADERBOARD")
    @GetMapping
    public ResponseEntity<ApiResponseWrapper<LeaderboardResponse>> getLeaderboard(
            @RequestParam @Parameter(description = "Leaderboard type (e.g. daily, weekly, alltime)") String type,
            @RequestParam(required = false) @Parameter(description = "Optional category ID to filter by") Long categoryId) {
        return leaderboardService.getLeaderboard(type, categoryId)
                .map(lb -> ApiResponseWrapper.success(lb, "Leaderboard retrieved successfully"))
                .orElse(ApiResponseWrapper.error(org.springframework.http.HttpStatus.NOT_FOUND, "Leaderboard not found"));
    }

    @Operation(summary = "Get leaderboard entries", description = "Retrieves paginated entries for a specific leaderboard")
    @ApiResponse(responseCode = "200", description = "Leaderboard entries retrieved successfully")
    @Auditable(action = "READ", resourceType = "LEADERBOARD")
    @GetMapping("/{leaderboardId}/entries")
    public ResponseEntity<ApiResponseWrapper<List<LeaderboardEntryResponse>>> getEntries(
            @PathVariable @Parameter(description = "ID of the leaderboard") Long leaderboardId,
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number (zero-based)") int page,
            @RequestParam(defaultValue = "50") @Parameter(description = "Page size") int size) {
        List<LeaderboardEntryResponse> entries = leaderboardService.getLeaderboardEntries(leaderboardId, page, size);
        return ApiResponseWrapper.success(entries, "Leaderboard entries retrieved successfully");
    }

    @Operation(summary = "Get global leaderboard", description = "Shortcut to retrieve the global leaderboard")
    @ApiResponse(responseCode = "200", description = "Global leaderboard retrieved successfully")
    @Auditable(action = "READ", resourceType = "LEADERBOARD")
    @GetMapping("/global")
    public ResponseEntity<ApiResponseWrapper<List<LeaderboardEntryResponse>>> getGlobalLeaderboard(
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number") int page,
            @RequestParam(defaultValue = "50") @Parameter(description = "Page size") int size) {
        List<LeaderboardEntryResponse> entries = leaderboardService.getLeaderboardByType("GLOBAL", null, page, size);
        return ApiResponseWrapper.success(entries, "Global leaderboard retrieved successfully");
    }

    @Operation(summary = "Get daily leaderboard", description = "Shortcut to retrieve the daily leaderboard")
    @ApiResponse(responseCode = "200", description = "Daily leaderboard retrieved successfully")
    @Auditable(action = "READ", resourceType = "LEADERBOARD")
    @GetMapping("/daily")
    public ResponseEntity<ApiResponseWrapper<List<LeaderboardEntryResponse>>> getDailyLeaderboard(
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number") int page,
            @RequestParam(defaultValue = "50") @Parameter(description = "Page size") int size) {
        List<LeaderboardEntryResponse> entries = leaderboardService.getLeaderboardByType("DAILY", null, page, size);
        return ApiResponseWrapper.success(entries, "Daily leaderboard retrieved successfully");
    }

    @Operation(summary = "Get weekly leaderboard", description = "Shortcut to retrieve the weekly leaderboard")
    @ApiResponse(responseCode = "200", description = "Weekly leaderboard retrieved successfully")
    @Auditable(action = "READ", resourceType = "LEADERBOARD")
    @GetMapping("/weekly")
    public ResponseEntity<ApiResponseWrapper<List<LeaderboardEntryResponse>>> getWeeklyLeaderboard(
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number") int page,
            @RequestParam(defaultValue = "50") @Parameter(description = "Page size") int size) {
        List<LeaderboardEntryResponse> entries = leaderboardService.getLeaderboardByType("WEEKLY", null, page, size);
        return ApiResponseWrapper.success(entries, "Weekly leaderboard retrieved successfully");
    }

    @Operation(summary = "Get monthly leaderboard", description = "Shortcut to retrieve the monthly leaderboard")
    @ApiResponse(responseCode = "200", description = "Monthly leaderboard retrieved successfully")
    @Auditable(action = "READ", resourceType = "LEADERBOARD")
    @GetMapping("/monthly")
    public ResponseEntity<ApiResponseWrapper<List<LeaderboardEntryResponse>>> getMonthlyLeaderboard(
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number") int page,
            @RequestParam(defaultValue = "50") @Parameter(description = "Page size") int size) {
        List<LeaderboardEntryResponse> entries = leaderboardService.getLeaderboardByType("MONTHLY", null, page, size);
        return ApiResponseWrapper.success(entries, "Monthly leaderboard retrieved successfully");
    }

    @Operation(summary = "Get friends leaderboard", description = "Retrieves the leaderboard entries for the current user's friends")
    @ApiResponse(responseCode = "200", description = "Friends leaderboard retrieved successfully")
    @Auditable(action = "READ", resourceType = "LEADERBOARD")
    @GetMapping("/friends")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<List<LeaderboardEntryResponse>>> getFriendsLeaderboard(
            @RequestParam(defaultValue = "GLOBAL") @Parameter(description = "Leaderboard type") String type,
            @RequestParam(required = false) @Parameter(description = "Optional category ID") Long categoryId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<LeaderboardEntryResponse> entries = leaderboardService.getFriendsLeaderboard(currentUser.getId(), type, categoryId);
        return ApiResponseWrapper.success(entries, "Friends leaderboard retrieved successfully");
    }
}
