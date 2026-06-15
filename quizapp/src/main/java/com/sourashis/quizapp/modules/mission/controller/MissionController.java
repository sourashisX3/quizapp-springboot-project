package com.sourashis.quizapp.modules.mission.controller;

import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.modules.auth.entity.User;
import com.sourashis.quizapp.modules.mission.dto.MissionResponse;
import com.sourashis.quizapp.modules.mission.service.MissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Missions", description = "Daily and weekly mission management endpoints")
@RestController
@RequestMapping("/api/v1/missions")
public class MissionController {

    @Autowired
    private MissionService missionService;

    @Operation(summary = "Get daily missions", description = "Retrieves the authenticated user's daily missions with progress")
    @ApiResponse(responseCode = "200", description = "Daily missions retrieved successfully")
    @GetMapping("/daily")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<List<MissionResponse>>> getDailyMissions(@AuthenticationPrincipal User user) {
        List<MissionResponse> missions = missionService.assignDailyMissions(user.getId());
        return ApiResponseWrapper.success(missions, "Daily missions retrieved successfully");
    }

    @Operation(summary = "Get weekly missions", description = "Retrieves the authenticated user's weekly missions with progress")
    @ApiResponse(responseCode = "200", description = "Weekly missions retrieved successfully")
    @GetMapping("/weekly")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<List<MissionResponse>>> getWeeklyMissions(@AuthenticationPrincipal User user) {
        List<MissionResponse> missions = missionService.assignWeeklyMissions(user.getId());
        return ApiResponseWrapper.success(missions, "Weekly missions retrieved successfully");
    }

    @Operation(summary = "Claim daily mission reward", description = "Claims the XP reward for a completed daily mission")
    @ApiResponse(responseCode = "200", description = "Reward claimed successfully")
    @PostMapping("/daily/claim/{missionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<Void>> claimDailyReward(
            @AuthenticationPrincipal User user,
            @PathVariable @Parameter(description = "Mission ID") Long missionId) {
        boolean claimed = missionService.claimMissionReward(user.getId(), missionId, "DAILY");
        if (!claimed) {
            return ApiResponseWrapper.error(org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Mission not completed or reward already claimed");
        }
        return ApiResponseWrapper.success(null, "Daily mission reward claimed successfully");
    }

    @Operation(summary = "Claim weekly mission reward", description = "Claims the XP reward for a completed weekly mission")
    @ApiResponse(responseCode = "200", description = "Reward claimed successfully")
    @PostMapping("/weekly/claim/{missionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<Void>> claimWeeklyReward(
            @AuthenticationPrincipal User user,
            @PathVariable @Parameter(description = "Mission ID") Long missionId) {
        boolean claimed = missionService.claimMissionReward(user.getId(), missionId, "WEEKLY");
        if (!claimed) {
            return ApiResponseWrapper.error(org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Mission not completed or reward already claimed");
        }
        return ApiResponseWrapper.success(null, "Weekly mission reward claimed successfully");
    }
}
