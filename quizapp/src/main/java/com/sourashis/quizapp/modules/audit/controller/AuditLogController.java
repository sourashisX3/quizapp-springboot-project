package com.sourashis.quizapp.modules.audit.controller;

import com.sourashis.quizapp.core.audit.Auditable;
import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.modules.audit.dto.AuditLogResponse;
import com.sourashis.quizapp.modules.audit.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Audit Logs", description = "Audit trail and activity tracking endpoints")
@RestController
@RequestMapping("/api/v1/audit")
@PreAuthorize("hasAuthority('audit:read')")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @Auditable(action = "READ", resourceType = "AUDIT")
    @Operation(summary = "Get audit logs for a specific user", description = "Retrieves paginated audit log entries for the specified user. Includes actions like login, logout, profile updates, and password changes. Requires the 'audit:read' permission.")
    @ApiResponse(responseCode = "200", description = "Audit logs retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Forbidden — requires 'audit:read' permission")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponseWrapper<List<AuditLogResponse>>> getByUser(
            @PathVariable @Parameter(description = "User ID to retrieve audit logs for", required = true, example = "1") Long userId,
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number (zero-based)", required = false, example = "0") int page,
            @RequestParam(defaultValue = "20") @Parameter(description = "Number of records per page", required = false, example = "20") int size) {
        List<AuditLogResponse> logs = auditLogService.getByUserId(userId, page, size);
        return ApiResponseWrapper.success(logs, "Audit logs retrieved successfully");
    }

    @Auditable(action = "READ", resourceType = "AUDIT")
    @Operation(summary = "Get audit logs filtered by action type", description = "Retrieves paginated audit log entries matching a specific action (e.g., LOGIN, LOGOUT, UPDATE_PROFILE). Requires the 'audit:read' permission.")
    @ApiResponse(responseCode = "200", description = "Audit logs retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Forbidden — requires 'audit:read' permission")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("/actions/{action}")
    public ResponseEntity<ApiResponseWrapper<List<AuditLogResponse>>> getByAction(
            @PathVariable @Parameter(description = "Action type to filter by (e.g., LOGIN, LOGOUT, UPDATE_PROFILE)", required = true, example = "LOGIN") String action,
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number (zero-based)", required = false, example = "0") int page,
            @RequestParam(defaultValue = "20") @Parameter(description = "Number of records per page", required = false, example = "20") int size) {
        List<AuditLogResponse> logs = auditLogService.getByAction(action, page, size);
        return ApiResponseWrapper.success(logs, "Audit logs retrieved successfully");
    }

    @Auditable(action = "READ", resourceType = "AUDIT")
    @Operation(summary = "Get audit logs filtered by resource", description = "Retrieves audit log entries for a specific resource type and ID combination. For example, view all audit events for a specific quiz or user. Requires the 'audit:read' permission.")
    @ApiResponse(responseCode = "200", description = "Audit logs retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Forbidden — requires 'audit:read' permission")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("/resources")
    public ResponseEntity<ApiResponseWrapper<List<AuditLogResponse>>> getByResource(
            @RequestParam @Parameter(description = "Resource type to filter by (e.g., QUIZ, USER)", required = true, example = "QUIZ") String resourceType,
            @RequestParam @Parameter(description = "Resource ID to filter by", required = true, example = "1") String resourceId) {
        List<AuditLogResponse> logs = auditLogService.getByResource(resourceType, resourceId);
        return ApiResponseWrapper.success(logs, "Audit logs retrieved successfully");
    }
}
