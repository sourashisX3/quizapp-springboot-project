package com.sourashis.quizapp.modules.mission.repository;

import com.sourashis.quizapp.modules.mission.entity.DailyMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DailyMissionRepository extends JpaRepository<DailyMission, Long> {

    List<DailyMission> findByIsActiveTrue();
}
