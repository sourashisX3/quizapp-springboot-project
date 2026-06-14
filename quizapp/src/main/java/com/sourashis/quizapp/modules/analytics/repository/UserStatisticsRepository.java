package com.sourashis.quizapp.modules.analytics.repository;

import com.sourashis.quizapp.modules.analytics.entity.UserStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserStatisticsRepository extends JpaRepository<UserStatistics, Long> {

    Optional<UserStatistics> findByUserId(Long userId);
}
