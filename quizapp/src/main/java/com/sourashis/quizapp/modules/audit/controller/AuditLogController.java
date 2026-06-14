package com.sourashis.quizapp.modules.audit.controller;

import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.modules.audit.dto.AuditLogResponse;
import com.sourashis.quizapp.modules.audit.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@PreAuthorize("hasAuthority('audit:read')")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponseWrapper<List<AuditLogResponse>>> getByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<AuditLogResponse> logs = auditLogService.getByUserId(userId, page, size);
        return ApiResponseWrapper.success(logs, "Audit logs retrieved successfully");
    }

    @GetMapping("/actions/{action}")
    public ResponseEntity<ApiResponseWrapper<List<AuditLogResponse>>> getByAction(
            @PathVariable String action,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<AuditLogResponse> logs = auditLogService.getByAction(action, page, size);
        return ApiResponseWrapper.success(logs, "Audit logs retrieved successfully");
    }

    @GetMapping("/resources")
    public ResponseEntity<ApiResponseWrapper<List<AuditLogResponse>>> getByResource(
            @RequestParam String resourceType,
            @RequestParam String resourceId) {
        List<AuditLogResponse> logs = auditLogService.getByResource(resourceType, resourceId);
        return ApiResponseWrapper.success(logs, "Audit logs retrieved successfully");
    }
}
