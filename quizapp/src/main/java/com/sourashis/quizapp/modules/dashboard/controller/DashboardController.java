package com.sourashis.quizapp.modules.dashboard.controller;

import com.sourashis.quizapp.core.audit.Auditable;
import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.modules.auth.entity.User;
import com.sourashis.quizapp.modules.dashboard.dto.AdminDashboardResponse;
import com.sourashis.quizapp.modules.dashboard.dto.ModeratorDashboardResponse;
import com.sourashis.quizapp.modules.dashboard.dto.UserDashboardResponse;
import com.sourashis.quizapp.modules.dashboard.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Dashboard", description = "User, admin, and moderator dashboard endpoints")
@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Operation(summary = "Get user dashboard", description = "Retrieves the authenticated user's personalized dashboard with stats and activity")
    @ApiResponse(responseCode = "200", description = "User dashboard retrieved successfully")
    @Auditable(action = "READ", resourceType = "USER")
    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<UserDashboardResponse>> getUserDashboard(
            @AuthenticationPrincipal User user) {
        UserDashboardResponse dashboard = dashboardService.getUserDashboard(user.getId());
        return ApiResponseWrapper.success(dashboard, "User dashboard retrieved successfully");
    }

    @Operation(summary = "Get admin dashboard", description = "Retrieves admin-level analytics and system overview")
    @ApiResponse(responseCode = "200", description = "Admin dashboard retrieved successfully")
    @Auditable(action = "READ", resourceType = "USER")
    @GetMapping("/admin")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponseWrapper<AdminDashboardResponse>> getAdminDashboard() {
        AdminDashboardResponse dashboard = dashboardService.getAdminDashboard();
        return ApiResponseWrapper.success(dashboard, "Admin dashboard retrieved successfully");
    }

    @Operation(summary = "Get moderator dashboard", description = "Retrieves moderator-level content management overview")
    @ApiResponse(responseCode = "200", description = "Moderator dashboard retrieved successfully")
    @Auditable(action = "READ", resourceType = "USER")
    @GetMapping("/moderator")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponseWrapper<ModeratorDashboardResponse>> getModeratorDashboard() {
        ModeratorDashboardResponse dashboard = dashboardService.getModeratorDashboard();
        return ApiResponseWrapper.success(dashboard, "Moderator dashboard retrieved successfully");
    }
}
