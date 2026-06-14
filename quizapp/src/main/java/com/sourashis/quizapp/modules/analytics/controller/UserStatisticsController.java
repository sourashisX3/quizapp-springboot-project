package com.sourashis.quizapp.modules.analytics.controller;

import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.modules.analytics.dto.UserStatisticsResponse;
import com.sourashis.quizapp.modules.analytics.service.UserStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class UserStatisticsController {

    @Autowired
    private UserStatisticsService userStatisticsService;

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasAuthority('analytics:read') or #userId == authentication.principal.id")
    public ResponseEntity<ApiResponseWrapper<UserStatisticsResponse>> getUserStatistics(@PathVariable Long userId) {
        UserStatisticsResponse response = userStatisticsService.getStatistics(userId);
        return ApiResponseWrapper.success(response, "User statistics retrieved successfully");
    }
}
