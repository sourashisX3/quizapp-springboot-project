package com.sourashis.quizapp.modules.leaderboard.service;

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

    public Optional<LeaderboardResponse> getLeaderboard(String type, Long categoryId) {
        return leaderboardRepository.findByLeaderboardTypeAndCategoryIdAndIsActiveTrue(type, categoryId)
                .map(this::toResponse);
    }

    public List<LeaderboardEntryResponse> getLeaderboardEntries(Long leaderboardId, int page, int size) {
        return leaderboardEntryRepository.findByLeaderboardIdOrderByPositionRank(leaderboardId, PageRequest.of(page, size))
                .stream().map(this::toEntryResponse).toList();
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
        return LeaderboardEntryResponse.builder()
                .userId(entry.getUserId())
                .score(entry.getScore())
                .rank(entry.getPositionRank())
                .metadataJson(entry.getMetadataJson())
                .build();
    }
}
