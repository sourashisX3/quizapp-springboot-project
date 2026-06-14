package com.sourashis.quizapp.modules.notification.repository;

import com.sourashis.quizapp.modules.notification.entity.NotificationDelivery;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface NotificationDeliveryRepository extends JpaRepository<NotificationDelivery, Long> {

    List<NotificationDelivery> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, String status);

    List<NotificationDelivery> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    long countByUserIdAndStatus(Long userId, String status);

    @Modifying
    @Query("update NotificationDelivery n set n.status='READ', n.readAt=?2 where n.userId=?1 and n.status='SENT'")
    int markAllAsRead(Long userId, Instant now);
}
