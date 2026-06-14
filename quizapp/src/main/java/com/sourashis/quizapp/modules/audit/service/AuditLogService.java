package com.sourashis.quizapp.modules.audit.service;

import com.sourashis.quizapp.modules.audit.dto.AuditLogResponse;
import com.sourashis.quizapp.modules.audit.entity.AuditLog;
import com.sourashis.quizapp.modules.audit.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public List<AuditLogResponse> getByUserId(Long userId, int page, int size) {
        return auditLogRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size))
                .stream().map(this::toResponse).toList();
    }

    public List<AuditLogResponse> getByAction(String action, int page, int size) {
        return auditLogRepository.findByActionOrderByCreatedAtDesc(action, PageRequest.of(page, size))
                .stream().map(this::toResponse).toList();
    }

    public List<AuditLogResponse> getByResource(String resourceType, String resourceId) {
        return auditLogRepository.findByResourceTypeAndResourceIdOrderByCreatedAtDesc(resourceType, resourceId)
                .stream().map(this::toResponse).toList();
    }

    private AuditLogResponse toResponse(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .uuid(log.getUuid())
                .userId(log.getUserId())
                .username(log.getUsername())
                .action(log.getAction())
                .resourceType(log.getResourceType())
                .resourceId(log.getResourceId())
                .ipAddress(log.getIpAddress())
                .userAgent(log.getUserAgent())
                .httpMethod(log.getHttpMethod())
                .httpPath(log.getHttpPath())
                .httpStatus(log.getHttpStatus())
                .executionTimeMs(log.getExecutionTimeMs())
                .isError(Boolean.TRUE.equals(log.getIsError()))
                .errorMessage(log.getErrorMessage())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
