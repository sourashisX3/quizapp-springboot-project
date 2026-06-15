package com.sourashis.quizapp.modules.reward.controller;

import com.sourashis.quizapp.core.audit.Auditable;
import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.modules.auth.entity.User;
import com.sourashis.quizapp.modules.reward.dto.AchievementResponse;
import com.sourashis.quizapp.modules.reward.dto.BadgeResponse;
import com.sourashis.quizapp.modules.reward.entity.Reward;
import com.sourashis.quizapp.modules.reward.service.RewardService;
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

@Tag(name = "Rewards", description = "Badges, achievements, and reward claiming endpoints")
@RestController
@RequestMapping("/api/v1/rewards")
public class RewardController {

    @Autowired
    private RewardService rewardService;

    @Operation(summary = "Get all badges", description = "Retrieves all available badges along with the current user's award status. Badges can be awarded, hidden, or locked. Hidden badges are only revealed once earned. Requires the 'reward:read' permission.")
    @ApiResponse(responseCode = "200", description = "Badges retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Forbidden — requires 'reward:read' permission")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Auditable(action = "READ", resourceType = "REWARD")
    @GetMapping("/badges")
    @PreAuthorize("hasAuthority('reward:read')")
    public ResponseEntity<ApiResponseWrapper<List<BadgeResponse>>> getBadges(@AuthenticationPrincipal @Parameter(hidden = true) User user) {
        List<BadgeResponse> badges = rewardService.getAllBadges(user.getId());
        return ApiResponseWrapper.success(badges, "Badges retrieved successfully");
    }

    @Operation(summary = "Get all achievements with progress", description = "Retrieves all achievements and the current user's progress toward each one. Each achievement includes a criteria type, target value, and the user's current progress. When progress meets the target, the achievement can be completed. Requires the 'reward:read' permission.")
    @ApiResponse(responseCode = "200", description = "Achievements retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Forbidden — requires 'reward:read' permission")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Auditable(action = "READ", resourceType = "REWARD")
    @GetMapping("/achievements")
    @PreAuthorize("hasAuthority('reward:read')")
    public ResponseEntity<ApiResponseWrapper<List<AchievementResponse>>> getAchievements(@AuthenticationPrincipal @Parameter(hidden = true) User user) {
        List<AchievementResponse> achievements = rewardService.getAllAchievements(user.getId());
        return ApiResponseWrapper.success(achievements, "Achievements retrieved successfully");
    }

    @Operation(summary = "Get pending rewards", description = "Retrieves all rewards that have been earned but not yet claimed by the authenticated user. Rewards must be explicitly claimed to receive the XP or other benefits. Requires the 'reward:read' permission.")
    @ApiResponse(responseCode = "200", description = "Pending rewards retrieved")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Forbidden — requires 'reward:read' permission")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Auditable(action = "READ", resourceType = "REWARD")
    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('reward:read')")
    public ResponseEntity<ApiResponseWrapper<List<Reward>>> getPendingRewards(@AuthenticationPrincipal @Parameter(hidden = true) User user) {
        List<Reward> rewards = rewardService.getPendingRewards(user.getId());
        return ApiResponseWrapper.success(rewards, "Pending rewards retrieved");
    }

    @Operation(summary = "Claim a pending reward", description = "Claims a specific reward by its ID. Only unclaimed rewards can be claimed. Once claimed, the reward's XP or benefits are credited to the user. Double-claiming is prevented. Requires the 'reward:claim' permission.")
    @ApiResponse(responseCode = "200", description = "Reward claimed successfully")
    @ApiResponse(responseCode = "400", description = "Reward has already been claimed")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Forbidden — requires 'reward:claim' permission")
    @ApiResponse(responseCode = "404", description = "Reward not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Auditable(action = "CLAIM", resourceType = "REWARD")
    @PostMapping("/{rewardId}/claim")
    @PreAuthorize("hasAuthority('reward:claim')")
    public ResponseEntity<ApiResponseWrapper<Void>> claimReward(@PathVariable @Parameter(description = "ID of the reward to claim") Long rewardId) {
        rewardService.claimReward(rewardId);
        return ApiResponseWrapper.success(null, "Reward claimed successfully");
    }
}
