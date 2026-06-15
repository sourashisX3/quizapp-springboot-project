package com.sourashis.quizapp.modules.reward.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sourashis.quizapp.modules.analytics.entity.UserStatistics;
import com.sourashis.quizapp.modules.analytics.repository.UserStatisticsRepository;
import com.sourashis.quizapp.modules.reward.entity.*;
import com.sourashis.quizapp.modules.reward.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GamificationService {

    @Autowired
    private LevelConfigRepository levelConfigRepository;
    @Autowired
    private BadgeRepository badgeRepository;
    @Autowired
    private AchievementRepository achievementRepository;
    @Autowired
    private UserBadgeRepository userBadgeRepository;
    @Autowired
    private UserAchievementRepository userAchievementRepository;
    @Autowired
    private RewardRepository rewardRepository;
    @Autowired
    private UserStatisticsRepository userStatisticsRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public int calculateLevel(Long totalXp) {
        Optional<LevelConfig> maxLevelConfig = levelConfigRepository.findTopByOrderByLevelDesc();
        if (maxLevelConfig.isEmpty()) {
            return (int) (Math.sqrt(totalXp / 100.0)) + 1;
        }
        List<LevelConfig> levels = levelConfigRepository.findByLevelLessThanEqualOrderByLevelDesc(
                maxLevelConfig.get().getLevel());
        for (LevelConfig lc : levels) {
            if (totalXp >= lc.getXpRequired()) {
                return lc.getLevel();
            }
        }
        return 1;
    }

    public List<Long> evaluateBadges(UserStatistics stats, Long userId) {
        List<Badge> badges = badgeRepository.findAll();
        List<Long> newlyAwarded = new ArrayList<>();
        for (Badge badge : badges) {
            if (userBadgeRepository.findByUserIdAndBadgeId(userId, badge.getId()).isPresent()) {
                continue;
            }
            try {
                JsonNode criteria = objectMapper.readTree(badge.getCriteriaJson());
                String type = criteria.get("type").asText();
                String operator = criteria.get("operator").asText();
                int value = criteria.get("value").asInt();
                if (evaluateCriteria(stats, type, operator, value)) {
                    awardBadge(userId, badge.getId(), null);
                    newlyAwarded.add(badge.getId());
                }
            } catch (JsonProcessingException e) {
                // skip badge with invalid criteria json
            }
        }
        return newlyAwarded;
    }

    public List<Long> evaluateAchievements(UserStatistics stats, Long userId) {
        List<Achievement> achievements = achievementRepository.findAll();
        List<Long> newlyCompleted = new ArrayList<>();
        for (Achievement achievement : achievements) {
            UserAchievement ua = userAchievementRepository
                    .findByUserIdAndAchievementId(userId, achievement.getId())
                    .orElseGet(() -> userAchievementRepository.save(UserAchievement.builder()
                            .userId(userId)
                            .achievementId(achievement.getId())
                            .progress(0)
                            .isCompleted(false)
                            .build()));
            if (Boolean.TRUE.equals(ua.getIsCompleted())) {
                continue;
            }
            int currentProgress = getStatValue(stats, achievement.getCriteriaType());
            ua.setProgress(Math.min(currentProgress, achievement.getCriteriaValue()));
            if (currentProgress >= achievement.getCriteriaValue()) {
                ua.setIsCompleted(true);
                ua.setCompletedAt(Instant.now());
                userAchievementRepository.save(ua);
                awardAchievement(userId, achievement.getId());
                newlyCompleted.add(achievement.getId());
            } else {
                userAchievementRepository.save(ua);
            }
        }
        return newlyCompleted;
    }

    public boolean awardBadge(Long userId, Long badgeId, String contextJson) {
        Badge badge = badgeRepository.findById(badgeId).orElse(null);
        if (badge == null) return false;
        if (userBadgeRepository.findByUserIdAndBadgeId(userId, badgeId).isPresent()) return false;
        UserBadge userBadge = UserBadge.builder()
                .userId(userId)
                .badgeId(badgeId)
                .contextJson(contextJson)
                .build();
        userBadgeRepository.save(userBadge);
        if (badge.getPointsReward() > 0) {
            createReward(userId, "POINTS", "BADGE", badgeId,
                    String.valueOf(badge.getPointsReward()), 0);
        }
        return true;
    }

    public boolean awardAchievement(Long userId, Long achievementId) {
        Achievement achievement = achievementRepository.findById(achievementId).orElse(null);
        if (achievement == null) return false;
        UserAchievement ua = userAchievementRepository
                .findByUserIdAndAchievementId(userId, achievementId)
                .orElse(null);
        if (ua == null || Boolean.TRUE.equals(ua.getIsCompleted())) return false;
        ua.setIsCompleted(true);
        ua.setCompletedAt(Instant.now());
        userAchievementRepository.save(ua);
        if (achievement.getXpReward() > 0) {
            createReward(userId, "XP", "ACHIEVEMENT", achievementId,
                    String.valueOf(achievement.getXpReward()), achievement.getXpReward());
        }
        return true;
    }

    public Reward createReward(Long userId, String rewardType, String sourceType,
                               Long sourceId, String rewardValue, Integer xpAmount) {
        Reward reward = Reward.builder()
                .userId(userId)
                .rewardType(rewardType)
                .sourceType(sourceType)
                .sourceId(sourceId)
                .rewardValue(rewardValue)
                .xpAmount(xpAmount != null ? xpAmount : 0)
                .build();
        return rewardRepository.save(reward);
    }

    public void updateLevel(UserStatistics stats) {
        int newLevel = calculateLevel(stats.getTotalXp());
        stats.setRankGlobal(newLevel);
    }

    private boolean evaluateCriteria(UserStatistics stats, String type, String operator, int value) {
        int statValue = getStatValue(stats, type);
        return switch (operator) {
            case ">=" -> statValue >= value;
            case ">" -> statValue > value;
            case "==" -> statValue == value;
            case "<=" -> statValue <= value;
            case "<" -> statValue < value;
            default -> false;
        };
    }

    private int getStatValue(UserStatistics stats, String type) {
        return switch (type) {
            case "QUIZZES_TAKEN" -> stats.getTotalQuizzesTaken();
            case "QUIZZES_PASSED" -> stats.getTotalQuizzesPassed();
            case "TOTAL_XP" -> stats.getTotalXp().intValue();
            case "STREAK" -> stats.getCurrentStreak();
            case "CONTESTS_WON" -> stats.getTotalContestsWon();
            case "CORRECT_ANSWERS" -> stats.getTotalCorrectAnswers();
            case "ACCURACY" -> stats.getTotalQuestionsAnswered() > 0
                    ? (int) Math.round(stats.getTotalCorrectAnswers() * 100.0 / stats.getTotalQuestionsAnswered())
                    : 0;
            default -> 0;
        };
    }
}
