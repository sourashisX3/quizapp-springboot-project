package com.sourashis.quizapp.modules.mission.repository;

import com.sourashis.quizapp.modules.mission.entity.WeeklyMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeeklyMissionRepository extends JpaRepository<WeeklyMission, Long> {

    List<WeeklyMission> findByIsActiveTrue();
}
