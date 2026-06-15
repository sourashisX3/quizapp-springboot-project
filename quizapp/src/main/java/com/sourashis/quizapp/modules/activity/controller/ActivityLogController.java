package com.sourashis.quizapp.modules.activity.controller;

import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.core.response.PaginationMeta;
import com.sourashis.quizapp.modules.activity.dto.ActivityLogResponse;
import com.sourashis.quizapp.modules.activity.service.ActivityLogService;
import com.sourashis.quizapp.modules.auth.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Activity Log", description = "User activity history endpoints")
@RestController
@RequestMapping("/api/v1/activity")
public class ActivityLogController {

    @Autowired
    private ActivityLogService activityLogService;

    @Operation(summary = "Get user activity", description = "Retrieves paginated activity history for the authenticated user")
    @ApiResponse(responseCode = "200", description = "Activity history retrieved successfully")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<List<ActivityLogResponse>>> getActivity(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number (0-based)") int page,
            @RequestParam(defaultValue = "20") @Parameter(description = "Page size") int size) {
        List<ActivityLogResponse> activities = activityLogService.getUserActivity(user.getId(), page, size);
        return ApiResponseWrapper.success(activities, "Activity history retrieved successfully");
    }
}
