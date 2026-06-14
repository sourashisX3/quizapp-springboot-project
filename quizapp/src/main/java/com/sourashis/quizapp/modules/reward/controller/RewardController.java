package com.sourashis.quizapp.modules.reward.controller;

import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.modules.auth.entity.User;
import com.sourashis.quizapp.modules.reward.dto.AchievementResponse;
import com.sourashis.quizapp.modules.reward.dto.BadgeResponse;
import com.sourashis.quizapp.modules.reward.entity.Reward;
import com.sourashis.quizapp.modules.reward.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rewards")
public class RewardController {

    @Autowired
    private RewardService rewardService;

    @GetMapping("/badges")
    @PreAuthorize("hasAuthority('reward:read')")
    public ResponseEntity<ApiResponseWrapper<List<BadgeResponse>>> getBadges(@AuthenticationPrincipal User user) {
        List<BadgeResponse> badges = rewardService.getAllBadges(user.getId());
        return ApiResponseWrapper.success(badges, "Badges retrieved successfully");
    }

    @GetMapping("/achievements")
    @PreAuthorize("hasAuthority('reward:read')")
    public ResponseEntity<ApiResponseWrapper<List<AchievementResponse>>> getAchievements(@AuthenticationPrincipal User user) {
        List<AchievementResponse> achievements = rewardService.getAllAchievements(user.getId());
        return ApiResponseWrapper.success(achievements, "Achievements retrieved successfully");
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('reward:read')")
    public ResponseEntity<ApiResponseWrapper<List<Reward>>> getPendingRewards(@AuthenticationPrincipal User user) {
        List<Reward> rewards = rewardService.getPendingRewards(user.getId());
        return ApiResponseWrapper.success(rewards, "Pending rewards retrieved");
    }

    @PostMapping("/{rewardId}/claim")
    @PreAuthorize("hasAuthority('reward:claim')")
    public ResponseEntity<ApiResponseWrapper<Void>> claimReward(@PathVariable Long rewardId) {
        rewardService.claimReward(rewardId);
        return ApiResponseWrapper.success(null, "Reward claimed successfully");
    }
}
