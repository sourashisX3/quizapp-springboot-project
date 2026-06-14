package com.sourashis.quizapp.modules.contest.repository;

import com.sourashis.quizapp.modules.contest.entity.ContestLeaderboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContestLeaderboardRepository extends JpaRepository<ContestLeaderboard, Long> {

    List<ContestLeaderboard> findByContestIdOrderByPositionRank(Long contestId);

    Optional<ContestLeaderboard> findByContestIdAndUserId(Long contestId, Long userId);
}
