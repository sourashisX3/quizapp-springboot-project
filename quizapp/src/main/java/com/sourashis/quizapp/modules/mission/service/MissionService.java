package com.sourashis.quizapp.modules.mission.service;

import com.sourashis.quizapp.modules.mission.dto.MissionResponse;
import com.sourashis.quizapp.modules.mission.entity.DailyMission;
import com.sourashis.quizapp.modules.mission.entity.UserMission;
import com.sourashis.quizapp.modules.mission.entity.WeeklyMission;
import com.sourashis.quizapp.modules.mission.repository.DailyMissionRepository;
import com.sourashis.quizapp.modules.mission.repository.UserMissionRepository;
import com.sourashis.quizapp.modules.mission.repository.WeeklyMissionRepository;
import com.sourashis.quizapp.modules.reward.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class MissionService {

    @Autowired
    private DailyMissionRepository dailyMissionRepository;

    @Autowired
    private WeeklyMissionRepository weeklyMissionRepository;

    @Autowired
    private UserMissionRepository userMissionRepository;

    @Autowired
    private RewardService rewardService;

    public List<MissionResponse> assignDailyMissions(Long userId) {
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        List<DailyMission> activeMissions = dailyMissionRepository.findByIsActiveTrue();
        List<MissionResponse> responses = new ArrayList<>();

        for (DailyMission mission : activeMissions) {
            UserMission existing = userMissionRepository
                    .findByUserIdAndMissionIdAndMissionType(userId, mission.getId(), "DAILY")
                    .orElse(null);
            if (existing == null) {
                UserMission userMission = UserMission.builder()
                        .userId(userId)
                        .missionId(mission.getId())
                        .missionType("DAILY")
                        .progress(0)
                        .targetValue(mission.getTargetValue())
                        .isCompleted(false)
                        .xpRewarded(false)
                        .build();
                userMission = userMissionRepository.save(userMission);
                responses.add(toResponse(userMission, mission.getTitle(), mission.getDescription(),
                        mission.getIconUrl(), mission.getMissionType(), mission.getXpReward()));
            } else {
                responses.add(toResponse(existing, mission.getTitle(), mission.getDescription(),
                        mission.getIconUrl(), mission.getMissionType(), mission.getXpReward()));
            }
        }
        return responses;
    }

    public List<MissionResponse> assignWeeklyMissions(Long userId) {
        List<WeeklyMission> activeMissions = weeklyMissionRepository.findByIsActiveTrue();
        List<MissionResponse> responses = new ArrayList<>();

        for (WeeklyMission mission : activeMissions) {
            UserMission existing = userMissionRepository
                    .findByUserIdAndMissionIdAndMissionType(userId, mission.getId(), "WEEKLY")
                    .orElse(null);
            if (existing == null) {
                UserMission userMission = UserMission.builder()
                        .userId(userId)
                        .missionId(mission.getId())
                        .missionType("WEEKLY")
                        .progress(0)
                        .targetValue(mission.getTargetValue())
                        .isCompleted(false)
                        .xpRewarded(false)
                        .build();
                userMission = userMissionRepository.save(userMission);
                responses.add(toResponse(userMission, mission.getTitle(), mission.getDescription(),
                        mission.getIconUrl(), mission.getMissionType(), mission.getXpReward()));
            } else {
                responses.add(toResponse(existing, mission.getTitle(), mission.getDescription(),
                        mission.getIconUrl(), mission.getMissionType(), mission.getXpReward()));
            }
        }
        return responses;
    }

    @Transactional(readOnly = true)
    public List<MissionResponse> getDailyMissions(Long userId) {
        List<UserMission> userMissions = userMissionRepository
                .findByUserIdAndMissionType(userId, "DAILY");
        return userMissions.stream()
                .map(um -> {
                    DailyMission dm = dailyMissionRepository.findById(um.getMissionId()).orElse(null);
                    if (dm == null) return null;
                    return toResponse(um, dm.getTitle(), dm.getDescription(),
                            dm.getIconUrl(), dm.getMissionType(), dm.getXpReward());
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MissionResponse> getWeeklyMissions(Long userId) {
        List<UserMission> userMissions = userMissionRepository
                .findByUserIdAndMissionType(userId, "WEEKLY");
        return userMissions.stream()
                .map(um -> {
                    WeeklyMission wm = weeklyMissionRepository.findById(um.getMissionId()).orElse(null);
                    if (wm == null) return null;
                    return toResponse(um, wm.getTitle(), wm.getDescription(),
                            wm.getIconUrl(), wm.getMissionType(), wm.getXpReward());
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    public void updateMissionProgress(Long userId, String period, String missionType, int increment) {
        List<UserMission> missions = userMissionRepository
                .findByUserIdAndMissionTypeAndIsCompletedFalse(userId, period.toUpperCase());

        for (UserMission mission : missions) {
            DailyMission dm = dailyMissionRepository.findById(mission.getMissionId()).orElse(null);
            WeeklyMission wm = weeklyMissionRepository.findById(mission.getMissionId()).orElse(null);

            String missionMissionType = dm != null ? dm.getMissionType() : (wm != null ? wm.getMissionType() : null);
            if (missionMissionType == null || !missionMissionType.equals(missionType)) continue;

            mission.setProgress(mission.getProgress() + increment);
            if (mission.getProgress() >= mission.getTargetValue()) {
                mission.setIsCompleted(true);
                mission.setCompletedAt(Instant.now());
            }
            userMissionRepository.save(mission);
        }
    }

    public boolean claimMissionReward(Long userId, Long missionId, String missionType) {
        UserMission mission = userMissionRepository
                .findByUserIdAndMissionIdAndMissionType(userId, missionId, missionType)
                .orElse(null);
        if (mission == null || !mission.getIsCompleted() || mission.getXpRewarded()) {
            return false;
        }

        int xpReward = 0;
        if ("DAILY".equals(missionType)) {
            DailyMission dm = dailyMissionRepository.findById(missionId).orElse(null);
            if (dm != null) xpReward = dm.getXpReward();
        } else {
            WeeklyMission wm = weeklyMissionRepository.findById(missionId).orElse(null);
            if (wm != null) xpReward = wm.getXpReward();
        }

        if (xpReward > 0) {
            rewardService.createReward(userId, "MISSION", missionType, missionId,
                    String.valueOf(xpReward) + " XP", xpReward);
        }

        mission.setXpRewarded(true);
        userMissionRepository.save(mission);
        return true;
    }

    private MissionResponse toResponse(UserMission um, String title, String description,
                                        String iconUrl, String missionType, int xpReward) {
        return MissionResponse.builder()
                .id(um.getMissionId())
                .title(title)
                .description(description)
                .iconUrl(iconUrl)
                .missionType(missionType)
                .targetValue(um.getTargetValue())
                .xpReward(xpReward)
                .progress(um.getProgress())
                .isCompleted(um.getIsCompleted())
                .completedAt(um.getCompletedAt())
                .missionCategory(um.getMissionType())
                .build();
    }
}
