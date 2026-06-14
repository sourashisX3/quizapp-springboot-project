package com.sourashis.quizapp.modules.contest.controller;

import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.modules.contest.dto.ContestRequest;
import com.sourashis.quizapp.modules.contest.dto.ContestResponse;
import com.sourashis.quizapp.modules.contest.entity.ContestParticipant;
import com.sourashis.quizapp.modules.contest.service.ContestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contests")
public class ContestController {

    @Autowired
    private ContestService contestService;

    @PostMapping
    @PreAuthorize("hasAuthority('contest:create')")
    public ResponseEntity<ApiResponseWrapper<ContestResponse>> createContest(@RequestBody ContestRequest req) {
        ContestResponse response = contestService.createContest(req);
        return ApiResponseWrapper.created(response, "Contest created successfully");
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponseWrapper<List<ContestResponse>>> getActiveContests() {
        List<ContestResponse> contests = contestService.getActiveContests();
        return ApiResponseWrapper.success(contests, "Active contests retrieved successfully");
    }

    @PostMapping("/{contestId}/join")
    @PreAuthorize("hasAuthority('contest:join')")
    public ResponseEntity<ApiResponseWrapper<String>> joinContest(@PathVariable Long contestId) {
        String result = contestService.joinContest(contestId);
        return ApiResponseWrapper.success(result, result);
    }

    @GetMapping("/{contestId}/participants")
    public ResponseEntity<ApiResponseWrapper<List<ContestParticipant>>> getParticipants(@PathVariable Long contestId) {
        List<ContestParticipant> participants = contestService.getParticipants(contestId);
        return ApiResponseWrapper.success(participants, "Participants retrieved successfully");
    }
}
