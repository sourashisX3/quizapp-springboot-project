package com.sourashis.quizapp.modules.reward.repository;

import com.sourashis.quizapp.modules.reward.entity.LevelConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LevelConfigRepository extends JpaRepository<LevelConfig, Long> {

    Optional<LevelConfig> findByLevel(Integer level);

    List<LevelConfig> findByLevelLessThanEqualOrderByLevelDesc(Integer level);

    Optional<LevelConfig> findTopByOrderByLevelDesc();
}
