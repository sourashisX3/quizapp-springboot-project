package com.sourashis.quizapp.modules.leaderboard.service;

import com.sourashis.quizapp.modules.analytics.entity.UserStatistics;
import com.sourashis.quizapp.modules.analytics.repository.UserStatisticsRepository;
import com.sourashis.quizapp.modules.leaderboard.entity.Leaderboard;
import com.sourashis.quizapp.modules.leaderboard.entity.LeaderboardEntry;
import com.sourashis.quizapp.modules.leaderboard.repository.LeaderboardEntryRepository;
import com.sourashis.quizapp.modules.leaderboard.repository.LeaderboardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LeaderboardScheduler {

    private static final Logger log = LoggerFactory.getLogger(LeaderboardScheduler.class);

    @Autowired
    private UserStatisticsRepository userStatisticsRepository;

    @Autowired
    private LeaderboardRepository leaderboardRepository;

    @Autowired
    private LeaderboardEntryRepository leaderboardEntryRepository;

    @Scheduled(fixedRate = 300000)
    @Transactional
    public void computeDailyLeaderboard() {
        computeLeaderboard("DAILY", null);
    }

    @Scheduled(cron = "0 0 0 * * MON")
    @Transactional
    public void computeWeeklyLeaderboard() {
        computeLeaderboard("WEEKLY", null);
    }

    @Scheduled(cron = "0 0 0 1 * *")
    @Transactional
    public void computeMonthlyLeaderboard() {
        computeLeaderboard("MONTHLY", null);
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanExpiredLeaderboards() {
        List<Leaderboard> expired = leaderboardRepository.findByLeaderboardTypeAndIsActiveTrue("DAILY");
        expired.addAll(leaderboardRepository.findByLeaderboardTypeAndIsActiveTrue("WEEKLY"));
        expired.addAll(leaderboardRepository.findByLeaderboardTypeAndIsActiveTrue("MONTHLY"));

        for (Leaderboard lb : expired) {
            lb.setIsActive(false);
        }
        leaderboardRepository.saveAll(expired);
        log.info("Deactivated {} expired leaderboards", expired.size());
    }

    public void computeLeaderboard(String type, Long categoryId) {
        List<UserStatistics> allStats = userStatisticsRepository.findAll();
        List<UserStatistics> topStats = allStats.stream()
                .sorted((a, b) -> Long.compare(b.getTotalXp(), a.getTotalXp()))
                .limit(100)
                .toList();

        Leaderboard leaderboard = Leaderboard.builder()
                .leaderboardType(type)
                .categoryId(categoryId)
                .periodStart(LocalDate.now())
                .periodEnd(LocalDate.now())
                .isActive(true)
                .build();
        leaderboard = leaderboardRepository.save(leaderboard);

        int rank = 1;
        for (UserStatistics stats : topStats) {
            LeaderboardEntry entry = LeaderboardEntry.builder()
                    .leaderboardId(leaderboard.getId())
                    .userId(stats.getUserId())
                    .score(stats.getTotalXp())
                    .positionRank(rank++)
                    .build();
            leaderboardEntryRepository.save(entry);
        }

        log.info("Computed {} leaderboard with {} entries", type, topStats.size());
    }
}
