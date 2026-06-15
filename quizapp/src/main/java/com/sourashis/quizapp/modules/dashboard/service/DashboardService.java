package com.sourashis.quizapp.modules.dashboard.service;

import com.sourashis.quizapp.modules.analytics.repository.UserStatisticsRepository;
import com.sourashis.quizapp.modules.auth.entity.User;
import com.sourashis.quizapp.modules.auth.repository.UserRepository;
import com.sourashis.quizapp.modules.contest.repository.ContestRepository;
import com.sourashis.quizapp.modules.dashboard.dto.AdminDashboardResponse;
import com.sourashis.quizapp.modules.dashboard.dto.ModeratorDashboardResponse;
import com.sourashis.quizapp.modules.dashboard.dto.UserDashboardResponse;
import com.sourashis.quizapp.modules.notification.repository.NotificationDeliveryRepository;
import com.sourashis.quizapp.modules.question.repository.QuestionRepository;
import com.sourashis.quizapp.modules.quiz.entity.QuizAttempt;
import com.sourashis.quizapp.modules.quiz.repository.QuizAttemptRepository;
import com.sourashis.quizapp.modules.quiz.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ContestRepository contestRepository;

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    @Autowired
    private NotificationDeliveryRepository notificationDeliveryRepository;

    @Autowired
    private UserStatisticsRepository userStatisticsRepository;

    public UserDashboardResponse getUserDashboard(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return UserDashboardResponse.builder().build();
        }

        var stats = userStatisticsRepository.findByUserId(userId).orElse(null);

        List<QuizAttempt> attempts = quizAttemptRepository.findByUserId(userId);
        attempts.sort(Comparator.comparing(QuizAttempt::getCreatedAt).reversed());
        List<QuizAttempt> recent = attempts.stream().limit(5).toList();

        long unreadCount = 0;
        try {
            unreadCount = notificationDeliveryRepository.countByUserIdAndStatus(userId, "SENT");
        } catch (Exception ignored) {}

        return UserDashboardResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .role(user.getRole() != null ? user.getRole().getName() : null)
                .totalQuizzesTaken(stats != null ? stats.getTotalQuizzesTaken() : 0)
                .totalQuizzesPassed(stats != null ? stats.getTotalQuizzesPassed() : 0)
                .totalContestsParticipated(stats != null ? stats.getTotalContestsParticipated() : 0)
                .totalContestsWon(stats != null ? stats.getTotalContestsWon() : 0)
                .totalXp(stats != null ? stats.getTotalXp() : 0L)
                .currentStreak(stats != null ? stats.getCurrentStreak() : 0)
                .badgesCount(stats != null ? stats.getBadgesCount() : 0)
                .achievementsCount(stats != null ? stats.getAchievementsCount() : 0)
                .averageScorePct(stats != null ? stats.getAverageScorePct() : null)
                .unreadNotifications((int) unreadCount)
                .recentAttempts(recent.stream().map(a -> UserDashboardResponse.RecentAttempt.builder()
                        .quizId(a.getQuizId())
                        .score(a.getScore())
                        .maxScore(a.getMaxScore())
                        .scorePct(a.getScorePct())
                        .passed(a.getPassed())
                        .status(a.getStatus())
                        .build()).toList())
                .build();
    }

    public AdminDashboardResponse getAdminDashboard() {
        long totalUsers = userRepository.count();
        long totalQuizzes = quizRepository.count();
        long totalQuestions = questionRepository.count();
        long totalContests = contestRepository.count();
        long activeContests = contestRepository.findByIsActiveTrueAndStartsAtBeforeAndEndsAtAfter(
                Instant.now(), Instant.now()).size();

        Instant todayStart = Instant.now().truncatedTo(ChronoUnit.DAYS);
        List<User> recentUsers = userRepository.findAll().stream()
                .filter(u -> u.getCreatedAt() != null && u.getCreatedAt().isAfter(todayStart))
                .sorted(Comparator.comparing(User::getCreatedAt).reversed())
                .limit(10)
                .toList();

        return AdminDashboardResponse.builder()
                .totalUsers(totalUsers)
                .totalQuizzes(totalQuizzes)
                .totalQuestions(totalQuestions)
                .totalContests(totalContests)
                .activeContests(activeContests)
                .newUsersToday(recentUsers.size())
                .recentRegistrations(recentUsers.stream().map(u -> AdminDashboardResponse.RecentRegistration.builder()
                        .username(u.getUsername())
                        .email(u.getEmail())
                        .role(u.getRole() != null ? u.getRole().getName() : null)
                        .createdAt(u.getCreatedAt() != null ? u.getCreatedAt().toString() : null)
                        .build()).toList())
                .build();
    }

    public ModeratorDashboardResponse getModeratorDashboard() {
        long totalQuestions = questionRepository.count();
        long totalQuizzes = quizRepository.count();
        long totalActiveContests = contestRepository.findByIsActiveTrueAndStartsAtBeforeAndEndsAtAfter(
                Instant.now(), Instant.now()).size();

        return ModeratorDashboardResponse.builder()
                .totalQuestions(totalQuestions)
                .totalQuizzes(totalQuizzes)
                .totalActiveContests(totalActiveContests)
                .build();
    }
}
