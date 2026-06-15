package com.sourashis.quizapp.modules.activity.service;

import com.sourashis.quizapp.modules.activity.dto.ActivityLogResponse;
import com.sourashis.quizapp.modules.activity.entity.ActivityLog;
import com.sourashis.quizapp.modules.activity.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ActivityLogService {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    public void logActivity(Long userId, String activityType, String description,
                            Long referenceId, String referenceType, String metadataJson) {
        ActivityLog log = ActivityLog.builder()
                .userId(userId)
                .activityType(activityType)
                .description(description)
                .referenceId(referenceId)
                .referenceType(referenceType)
                .metadataJson(metadataJson)
                .createdAt(Instant.now())
                .build();
        activityLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<ActivityLogResponse> getUserActivity(Long userId, int page, int size) {
        return activityLogRepository
                .findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size))
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ActivityLogResponse toResponse(ActivityLog log) {
        return ActivityLogResponse.builder()
                .id(log.getId())
                .activityType(log.getActivityType())
                .description(log.getDescription())
                .referenceId(log.getReferenceId())
                .referenceType(log.getReferenceType())
                .metadataJson(log.getMetadataJson())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
