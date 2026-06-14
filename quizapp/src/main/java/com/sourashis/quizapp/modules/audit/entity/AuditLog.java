package com.sourashis.quizapp.modules.audit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(columnList = "createdAt DESC"),
        @Index(columnList = "userId, createdAt DESC"),
        @Index(columnList = "action, createdAt DESC"),
        @Index(columnList = "resourceType, resourceId")
})
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String uuid;

    private Long userId;

    private String username;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String resourceType;

    private String resourceId;

    @Column(columnDefinition = "TEXT")
    private String detailsJson;

    @Column(nullable = false, length = 45)
    private String ipAddress;

    @Column(length = 500)
    private String userAgent;

    @Column(length = 36)
    private String requestId;

    @Column(length = 10)
    private String httpMethod;

    @Column(length = 500)
    private String httpPath;

    private Integer httpStatus;

    private Integer executionTimeMs;

    @Builder.Default
    private Boolean isError = false;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Column(updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
