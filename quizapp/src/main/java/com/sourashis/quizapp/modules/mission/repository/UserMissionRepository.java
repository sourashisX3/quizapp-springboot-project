package com.sourashis.quizapp.modules.mission.repository;

import com.sourashis.quizapp.modules.mission.entity.UserMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMissionRepository extends JpaRepository<UserMission, Long> {

    List<UserMission> findByUserIdAndMissionTypeAndIsCompletedFalse(Long userId, String missionType);

    Optional<UserMission> findByUserIdAndMissionIdAndMissionType(Long userId, Long missionId, String missionType);

    List<UserMission> findByUserIdAndMissionType(Long userId, String missionType);
}
