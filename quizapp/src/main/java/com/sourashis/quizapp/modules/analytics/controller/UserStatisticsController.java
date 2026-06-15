package com.sourashis.quizapp.modules.analytics.controller;

import com.sourashis.quizapp.core.audit.Auditable;
import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.modules.analytics.dto.UserStatisticsResponse;
import com.sourashis.quizapp.modules.analytics.service.UserStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Analytics", description = "User statistics and analytics endpoints")
@RestController
@RequestMapping("/api/v1/analytics")
public class UserStatisticsController {

    @Autowired
    private UserStatisticsService userStatisticsService;

    @Operation(summary = "Get user statistics", description = "Retrieves comprehensive statistics for a specific user. Includes quizzes taken/passed, contests participated/won, XP, streaks, accuracy, and more. Users can view their own stats; viewing others' stats requires the 'analytics:read' permission.")
    @ApiResponse(responseCode = "200", description = "User statistics retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Forbidden — requires 'analytics:read' permission to view other users' stats")
    @ApiResponse(responseCode = "404", description = "User not found or statistics not available")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Auditable(action = "READ", resourceType = "USER")
    @GetMapping("/users/{userId}")
    @PreAuthorize("hasAuthority('analytics:read') or #userId == authentication.principal.id")
    public ResponseEntity<ApiResponseWrapper<UserStatisticsResponse>> getUserStatistics(@PathVariable @Parameter(description = "ID of the user to get statistics for") Long userId) {
        UserStatisticsResponse response = userStatisticsService.getStatistics(userId);
        return ApiResponseWrapper.success(response, "User statistics retrieved successfully");
    }
}
