package com.sourashis.quizapp.modules.leaderboard.controller;

import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.modules.leaderboard.dto.LeaderboardEntryResponse;
import com.sourashis.quizapp.modules.leaderboard.dto.LeaderboardResponse;
import com.sourashis.quizapp.modules.leaderboard.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@PreAuthorize("hasAuthority('leaderboard:read')")
public class LeaderboardController {

    @Autowired
    private LeaderboardService leaderboardService;

    @GetMapping
    public ResponseEntity<ApiResponseWrapper<LeaderboardResponse>> getLeaderboard(
            @RequestParam String type,
            @RequestParam(required = false) Long categoryId) {
        return leaderboardService.getLeaderboard(type, categoryId)
                .map(lb -> ApiResponseWrapper.success(lb, "Leaderboard retrieved successfully"))
                .orElse(ApiResponseWrapper.error(org.springframework.http.HttpStatus.NOT_FOUND, "Leaderboard not found"));
    }

    @GetMapping("/{leaderboardId}/entries")
    public ResponseEntity<ApiResponseWrapper<List<LeaderboardEntryResponse>>> getEntries(
            @PathVariable Long leaderboardId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        List<LeaderboardEntryResponse> entries = leaderboardService.getLeaderboardEntries(leaderboardId, page, size);
        return ApiResponseWrapper.success(entries, "Leaderboard entries retrieved successfully");
    }
}
