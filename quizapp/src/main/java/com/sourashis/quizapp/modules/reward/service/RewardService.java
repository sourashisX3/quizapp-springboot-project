package com.sourashis.quizapp.modules.reward.service;

import com.sourashis.quizapp.modules.reward.dto.AchievementResponse;
import com.sourashis.quizapp.modules.reward.dto.BadgeResponse;
import com.sourashis.quizapp.modules.reward.entity.*;
import com.sourashis.quizapp.modules.reward.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class RewardService {

    @Autowired private BadgeRepository badgeRepository;
    @Autowired private AchievementRepository achievementRepository;
    @Autowired private UserBadgeRepository userBadgeRepository;
    @Autowired private UserAchievementRepository userAchievementRepository;
    @Autowired private RewardRepository rewardRepository;

    @Transactional(readOnly = true)
    public List<BadgeResponse> getAllBadges(Long userId) {
        return badgeRepository.findAll().stream().map(badge -> {
            boolean isAwarded = userBadgeRepository.findByUserIdAndBadgeId(userId, badge.getId()).isPresent();
            Instant awardedAt = userBadgeRepository.findByUserIdAndBadgeId(userId, badge.getId())
                    .map(UserBadge::getAwardedAt).orElse(null);
            return BadgeResponse.builder()
                    .id(badge.getId()).name(badge.getName()).description(badge.getDescription())
                    .iconUrl(badge.getIconUrl()).badgeType(badge.getBadgeType())
                    .pointsReward(badge.getPointsReward()).isHidden(badge.getIsHidden())
                    .createdAt(badge.getCreatedAt()).isAwarded(isAwarded).awardedAt(awardedAt).build();
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<AchievementResponse> getAllAchievements(Long userId) {
        return achievementRepository.findAll().stream().map(achievement -> {
            UserAchievement ua = userAchievementRepository.findByUserIdAndAchievementId(userId, achievement.getId()).orElse(null);
            return AchievementResponse.builder()
                    .id(achievement.getId()).name(achievement.getName()).description(achievement.getDescription())
                    .iconUrl(achievement.getIconUrl()).criteriaType(achievement.getCriteriaType())
                    .criteriaValue(achievement.getCriteriaValue()).xpReward(achievement.getXpReward())
                    .createdAt(achievement.getCreatedAt())
                    .progress(ua != null ? ua.getProgress() : 0)
                    .isCompleted(ua != null && Boolean.TRUE.equals(ua.getIsCompleted()))
                    .completedAt(ua != null ? ua.getCompletedAt() : null).build();
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<Reward> getPendingRewards(Long userId) {
        return rewardRepository.findByUserIdAndClaimedFalse(userId);
    }

    public void claimReward(Long rewardId) {
        Reward reward = rewardRepository.findById(rewardId).orElseThrow();
        reward.setClaimed(true);
        reward.setClaimedAt(Instant.now());
        rewardRepository.save(reward);
    }
}
