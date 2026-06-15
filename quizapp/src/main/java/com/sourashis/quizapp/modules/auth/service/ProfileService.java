package com.sourashis.quizapp.modules.auth.service;

import com.sourashis.quizapp.modules.analytics.entity.UserStatistics;
import com.sourashis.quizapp.modules.analytics.repository.UserStatisticsRepository;
import com.sourashis.quizapp.modules.auth.dto.*;
import com.sourashis.quizapp.modules.auth.entity.User;
import com.sourashis.quizapp.modules.auth.exception.UserNotFoundException;
import com.sourashis.quizapp.modules.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStatisticsRepository userStatisticsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        UserStatistics stats = userStatisticsRepository.findByUserId(userId).orElse(null);
        return toProfileResponse(user, stats);
    }

    public PublicProfileResponse getPublicProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        UserStatistics stats = userStatisticsRepository.findByUserId(userId).orElse(null);
        return toPublicProfileResponse(user, stats);
    }

    public ProfileResponse updateProfile(Long userId, ProfileUpdateRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (req.getDisplayName() != null) {
            user.setDisplayName(req.getDisplayName());
        }
        if (req.getBio() != null) {
            user.setBio(req.getBio());
        }
        if (req.getPhoneNumber() != null) {
            user.setPhoneNumber(req.getPhoneNumber());
        }
        if (req.getAddress() != null) {
            user.setAddress(req.getAddress());
        }
        if (req.getSocialLinksJson() != null) {
            user.setSocialLinksJson(req.getSocialLinksJson());
        }

        user = userRepository.save(user);
        UserStatistics stats = userStatisticsRepository.findByUserId(userId).orElse(null);
        return toProfileResponse(user, stats);
    }

    public ProfileResponse updateAvatar(Long userId, String avatarUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        user.setProfilePictureUrl(avatarUrl);
        user = userRepository.save(user);
        UserStatistics stats = userStatisticsRepository.findByUserId(userId).orElse(null);
        return toProfileResponse(user, stats);
    }

    public UserStatsResponse getStats(Long userId) {
        UserStatistics stats = userStatisticsRepository.findByUserId(userId)
                .orElse(null);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return toUserStatsResponse(user, stats);
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public int calculateLevel(Long xp) {
        long[] thresholds = {0L, 100L, 250L, 500L, 1000L, 2000L, 4000L, 8000L, 16000L, 32000L, 64000L, 128000L};
        int level = 1;
        for (int i = 0; i < thresholds.length; i++) {
            if (xp >= thresholds[i]) {
                level = i + 1;
            } else {
                break;
            }
        }
        return level;
    }

    public long getXpForNextLevel(int level) {
        return (long) Math.ceil(100.0 * level * 1.5);
    }

    private ProfileResponse toProfileResponse(User user, UserStatistics stats) {
        ProfileResponse.ProfileResponseBuilder builder = ProfileResponse.builder()
                .id(user.getId())
                .uuid(user.getUuid())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .bio(user.getBio())
                .profilePictureUrl(user.getProfilePictureUrl())
                .socialLinksJson(user.getSocialLinksJson())
                .phoneNumber(user.getPhoneNumber())
                .accountStatus(user.getAccountStatus())
                .emailVerified(user.isEmailVerified())
                .level(user.getLevel())
                .currentXp(user.getCurrentXp())
                .xpForNextLevel(user.getXpForNextLevel())
                .role(user.getRole() != null ? user.getRole().getName() : null)
                .joinedDate(user.getCreatedAt());

        if (stats != null) {
            builder.currentStreak(stats.getCurrentStreak())
                    .longestStreak(stats.getLongestStreak())
                    .totalXp(stats.getTotalXp())
                    .totalQuizzesTaken(stats.getTotalQuizzesTaken())
                    .totalQuizzesPassed(stats.getTotalQuizzesPassed())
                    .badgesCount(stats.getBadgesCount())
                    .achievementsCount(stats.getAchievementsCount())
                    .rankGlobal(stats.getRankGlobal());
        }

        return builder.build();
    }

    private PublicProfileResponse toPublicProfileResponse(User user, UserStatistics stats) {
        PublicProfileResponse.PublicProfileResponseBuilder builder = PublicProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .bio(user.getBio())
                .profilePictureUrl(user.getProfilePictureUrl())
                .level(user.getLevel())
                .currentXp(user.getCurrentXp())
                .xpForNextLevel(user.getXpForNextLevel())
                .joinedDate(user.getCreatedAt());

        if (stats != null) {
            builder.totalXp(stats.getTotalXp())
                    .badgesCount(stats.getBadgesCount())
                    .achievementsCount(stats.getAchievementsCount())
                    .rankGlobal(stats.getRankGlobal())
                    .totalQuizzesPassed(stats.getTotalQuizzesPassed())
                    .totalQuizzesTaken(stats.getTotalQuizzesTaken())
                    .averageScorePct(stats.getAverageScorePct());
        }

        return builder.build();
    }

    private UserStatsResponse toUserStatsResponse(User user, UserStatistics stats) {
        UserStatsResponse.UserStatsResponseBuilder builder = UserStatsResponse.builder()
                .level(user.getLevel());

        if (stats != null) {
            double accuracy = stats.getTotalQuestionsAnswered() > 0
                    ? (double) stats.getTotalCorrectAnswers() / stats.getTotalQuestionsAnswered() * 100
                    : 0.0;

            builder.totalQuizzesTaken(stats.getTotalQuizzesTaken())
                    .totalQuizzesPassed(stats.getTotalQuizzesPassed())
                    .totalQuestionsAnswered(stats.getTotalQuestionsAnswered())
                    .totalCorrectAnswers(stats.getTotalCorrectAnswers())
                    .accuracyPercentage(Math.round(accuracy * 100.0) / 100.0)
                    .currentStreak(stats.getCurrentStreak())
                    .longestStreak(stats.getLongestStreak())
                    .totalXp(stats.getTotalXp())
                    .totalScore(stats.getTotalScore())
                    .totalContestsParticipated(stats.getTotalContestsParticipated())
                    .totalContestsWon(stats.getTotalContestsWon())
                    .averageScorePct(stats.getAverageScorePct())
                    .averageTimePerQuestionSec(stats.getAverageTimePerQuestionSec())
                    .badgesCount(stats.getBadgesCount())
                    .achievementsCount(stats.getAchievementsCount())
                    .rankGlobal(stats.getRankGlobal())
                    .rankMonthly(stats.getRankMonthly())
                    .categoryPerformance(new HashMap<>());
        }

        return builder.build();
    }
}
