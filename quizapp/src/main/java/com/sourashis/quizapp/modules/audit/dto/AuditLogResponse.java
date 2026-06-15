package com.sourashis.quizapp.modules.audit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {
    @Schema(description = "Audit log entry ID", example = "1")
    private Long id;
    @Schema(description = "Audit log entry UUID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String uuid;
    @Schema(description = "ID of the user who performed the action", example = "1")
    private Long userId;
    @Schema(description = "Username of the user who performed the action", example = "john_doe")
    private String username;
    @Schema(description = "Type of action performed (LOGIN, LOGOUT, CREATE, UPDATE, DELETE)", example = "LOGIN")
    private String action;
    @Schema(description = "Type of resource affected", example = "USER")
    private String resourceType;
    @Schema(description = "ID of the resource affected", example = "1")
    private String resourceId;
    @Schema(description = "IP address from which the action was performed", example = "192.168.1.100")
    private String ipAddress;
    @Schema(description = "User agent string of the client", example = "Mozilla/5.0 QuizApp/1.0")
    private String userAgent;
    @Schema(description = "HTTP method used (GET, POST, PUT, DELETE, PATCH)", example = "POST")
    private String httpMethod;
    @Schema(description = "HTTP path of the request", example = "/api/v1/auth/login")
    private String httpPath;
    @Schema(description = "HTTP status code returned", example = "200")
    private Integer httpStatus;
    @Schema(description = "Time taken to process the request in milliseconds", example = "145")
    private Integer executionTimeMs;
    @Schema(description = "Whether the action resulted in an error", example = "false")
    private boolean isError;
    @Schema(description = "Error message if the action failed (null if successful)", example = "null")
    private String errorMessage;
    @Schema(description = "Timestamp when the action occurred", example = "2026-06-15T10:30:00Z")
    private Instant createdAt;
}
