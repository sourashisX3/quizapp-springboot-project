package com.sourashis.quizapp.modules.leaderboard.repository;

import com.sourashis.quizapp.modules.leaderboard.entity.Leaderboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaderboardRepository extends JpaRepository<Leaderboard, Long> {

    Optional<Leaderboard> findByLeaderboardTypeAndCategoryIdAndIsActiveTrue(String leaderboardType, Long categoryId);

    List<Leaderboard> findByLeaderboardTypeAndIsActiveTrue(String leaderboardType);
}
