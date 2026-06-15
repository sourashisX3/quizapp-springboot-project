package com.sourashis.quizapp.modules.activity.repository;

import com.sourashis.quizapp.modules.activity.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    Page<ActivityLog> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<ActivityLog> findByUserIdAndActivityType(Long userId, String activityType, Pageable pageable);

    long countByUserIdAndCreatedAtAfter(Long userId, Instant after);
}
