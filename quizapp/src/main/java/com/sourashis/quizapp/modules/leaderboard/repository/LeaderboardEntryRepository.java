package com.sourashis.quizapp.modules.leaderboard.repository;

import com.sourashis.quizapp.modules.leaderboard.entity.LeaderboardEntry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaderboardEntryRepository extends JpaRepository<LeaderboardEntry, Long> {

    List<LeaderboardEntry> findByLeaderboardIdOrderByPositionRank(Long leaderboardId, Pageable pageable);

    Optional<LeaderboardEntry> findByLeaderboardIdAndUserId(Long leaderboardId, Long userId);

    List<LeaderboardEntry> findByUserId(Long userId);

    void deleteByLeaderboardId(Long leaderboardId);
}
