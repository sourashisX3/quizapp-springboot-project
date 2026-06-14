package com.sourashis.quizapp.modules.audit.dto;

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
    private Long id;
    private String uuid;
    private Long userId;
    private String username;
    private String action;
    private String resourceType;
    private String resourceId;
    private String ipAddress;
    private String userAgent;
    private String httpMethod;
    private String httpPath;
    private Integer httpStatus;
    private Integer executionTimeMs;
    private boolean isError;
    private String errorMessage;
    private Instant createdAt;
}
