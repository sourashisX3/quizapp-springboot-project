package com.sourashis.quizapp.modules.leaderboard.service;

import com.sourashis.quizapp.modules.auth.entity.User;
import com.sourashis.quizapp.modules.auth.repository.UserRepository;
import com.sourashis.quizapp.modules.leaderboard.dto.LeaderboardEntryResponse;
import com.sourashis.quizapp.modules.leaderboard.dto.LeaderboardResponse;
import com.sourashis.quizapp.modules.leaderboard.entity.Leaderboard;
import com.sourashis.quizapp.modules.leaderboard.entity.LeaderboardEntry;
import com.sourashis.quizapp.modules.leaderboard.repository.LeaderboardEntryRepository;
import com.sourashis.quizapp.modules.leaderboard.repository.LeaderboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class LeaderboardService {

    @Autowired
    private LeaderboardRepository leaderboardRepository;

    @Autowired
    private LeaderboardEntryRepository leaderboardEntryRepository;

    @Autowired
    private UserRepository userRepository;

    public Optional<LeaderboardResponse> getLeaderboard(String type, Long categoryId) {
        return leaderboardRepository.findByLeaderboardTypeAndCategoryIdAndIsActiveTrue(type, categoryId)
                .map(this::toResponse);
    }

    public List<LeaderboardEntryResponse> getLeaderboardEntries(Long leaderboardId, int page, int size) {
        return leaderboardEntryRepository.findByLeaderboardIdOrderByPositionRank(leaderboardId, PageRequest.of(page, size))
                .stream().map(this::toEntryResponse).toList();
    }

    public List<LeaderboardEntryResponse> getLeaderboardByType(String type, Long categoryId, int page, int size) {
        return leaderboardRepository.findByLeaderboardTypeAndCategoryIdAndIsActiveTrue(type, categoryId)
                .map(lb -> leaderboardEntryRepository.findByLeaderboardIdOrderByPositionRank(lb.getId(), PageRequest.of(page, size))
                        .stream().map(this::toEntryResponse).toList())
                .orElse(List.of());
    }

    public List<LeaderboardEntryResponse> getFriendsLeaderboard(Long userId, String type, Long categoryId) {
        return leaderboardRepository.findByLeaderboardTypeAndCategoryIdAndIsActiveTrue(type, categoryId)
                .map(lb -> leaderboardEntryRepository.findByLeaderboardIdOrderByPositionRank(lb.getId(), PageRequest.of(0, 100))
                        .stream().map(this::toEntryResponse).toList())
                .orElse(List.of());
    }

    private LeaderboardResponse toResponse(Leaderboard lb) {
        return LeaderboardResponse.builder()
                .id(lb.getId())
                .leaderboardType(lb.getLeaderboardType())
                .categoryId(lb.getCategoryId())
                .periodStart(lb.getPeriodStart() != null ? lb.getPeriodStart().toString() : null)
                .periodEnd(lb.getPeriodEnd() != null ? lb.getPeriodEnd().toString() : null)
                .isActive(lb.getIsActive())
                .totalEntries((int) leaderboardEntryRepository.count())
                .createdAt(lb.getCreatedAt())
                .build();
    }

    private LeaderboardEntryResponse toEntryResponse(LeaderboardEntry entry) {
        String username = null;
        String displayName = null;
        if (entry.getUserId() != null) {
            User user = userRepository.findById(entry.getUserId()).orElse(null);
            if (user != null) {
                username = user.getUsername();
                displayName = user.getDisplayName();
            }
        }

        return LeaderboardEntryResponse.builder()
                .userId(entry.getUserId())
                .username(username)
                .displayName(displayName)
                .score(entry.getScore())
                .rank(entry.getPositionRank())
                .metadataJson(entry.getMetadataJson())
                .build();
    }
}
