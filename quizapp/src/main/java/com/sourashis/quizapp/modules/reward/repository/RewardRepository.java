package com.sourashis.quizapp.modules.reward.repository;

import com.sourashis.quizapp.modules.reward.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {

    List<Reward> findByUserId(Long userId);

    List<Reward> findByUserIdAndClaimedFalse(Long userId);

    List<Reward> findByUserIdAndSourceTypeAndSourceId(Long userId, String sourceType, Long sourceId);
}
