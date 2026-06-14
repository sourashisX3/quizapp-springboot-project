package com.sourashis.quizapp.modules.analytics.service;

import com.sourashis.quizapp.modules.analytics.dto.UserStatisticsResponse;
import com.sourashis.quizapp.modules.analytics.entity.UserStatistics;
import com.sourashis.quizapp.modules.analytics.repository.UserStatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserStatisticsService {

    @Autowired
    private UserStatisticsRepository userStatisticsRepository;

    public UserStatisticsResponse getStatistics(Long userId) {
        UserStatistics stats = userStatisticsRepository.findByUserId(userId)
                .orElseGet(() -> UserStatistics.builder().userId(userId).build());
        return toResponse(stats);
    }

    private UserStatisticsResponse toResponse(UserStatistics stats) {
        return UserStatisticsResponse.builder()
                .userId(stats.getUserId())
                .totalQuizzesTaken(stats.getTotalQuizzesTaken())
                .totalQuizzesPassed(stats.getTotalQuizzesPassed())
                .totalQuestionsAnswered(stats.getTotalQuestionsAnswered())
                .totalCorrectAnswers(stats.getTotalCorrectAnswers())
                .totalScore(stats.getTotalScore())
                .totalXp(stats.getTotalXp())
                .currentStreak(stats.getCurrentStreak())
                .longestStreak(stats.getLongestStreak())
                .totalContestsParticipated(stats.getTotalContestsParticipated())
                .totalContestsWon(stats.getTotalContestsWon())
                .averageScorePct(stats.getAverageScorePct())
                .badgesCount(stats.getBadgesCount())
                .achievementsCount(stats.getAchievementsCount())
                .build();
    }
}
