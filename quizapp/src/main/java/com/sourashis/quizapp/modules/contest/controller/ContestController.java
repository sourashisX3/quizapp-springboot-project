package com.sourashis.quizapp.modules.contest.controller;

import com.sourashis.quizapp.core.audit.Auditable;
import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.modules.auth.entity.User;
import com.sourashis.quizapp.modules.contest.dto.ContestRequest;
import com.sourashis.quizapp.modules.contest.dto.ContestResponse;
import com.sourashis.quizapp.modules.contest.entity.ContestParticipant;
import com.sourashis.quizapp.modules.contest.service.ContestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Contests", description = "Create, manage, join, and view contest endpoints")
@RestController
@RequestMapping("/api/v1/contests")
public class ContestController {

    @Autowired
    private ContestService contestService;

    @Operation(summary = "Create a new contest", description = "Creates a new contest with the provided configuration")
    @ApiResponse(responseCode = "201", description = "Contest created successfully")
    @Auditable(action = "CREATE", resourceType = "CONTEST")
    @PostMapping
    @PreAuthorize("hasAuthority('contest:create')")
    public ResponseEntity<ApiResponseWrapper<ContestResponse>> createContest(@Valid @RequestBody ContestRequest req) {
        ContestResponse response = contestService.createContest(req);
        return ApiResponseWrapper.created(response, "Contest created successfully");
    }

    @Operation(summary = "Get all contests", description = "Retrieves a list of all contests")
    @ApiResponse(responseCode = "200", description = "Contests retrieved successfully")
    @Auditable(action = "READ", resourceType = "CONTEST")
    @GetMapping("/all")
    public ResponseEntity<ApiResponseWrapper<List<ContestResponse>>> getAllContests() {
        List<ContestResponse> contests = contestService.getAllContests();
        return ApiResponseWrapper.success(contests, "All contests retrieved successfully");
    }

    @Operation(summary = "Get active contests", description = "Retrieves contests that are currently in progress")
    @ApiResponse(responseCode = "200", description = "Active contests retrieved successfully")
    @Auditable(action = "READ", resourceType = "CONTEST")
    @GetMapping
    public ResponseEntity<ApiResponseWrapper<List<ContestResponse>>> getActiveContests() {
        List<ContestResponse> contests = contestService.getActiveContests();
        return ApiResponseWrapper.success(contests, "Active contests retrieved successfully");
    }

    @Operation(summary = "Get upcoming contests", description = "Retrieves contests that are scheduled to start in the future")
    @ApiResponse(responseCode = "200", description = "Upcoming contests retrieved successfully")
    @Auditable(action = "READ", resourceType = "CONTEST")
    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponseWrapper<List<ContestResponse>>> getUpcomingContests() {
        List<ContestResponse> contests = contestService.getUpcomingContests();
        return ApiResponseWrapper.success(contests, "Upcoming contests retrieved successfully");
    }

    @Operation(summary = "Get completed contests", description = "Retrieves contests that have already ended")
    @ApiResponse(responseCode = "200", description = "Completed contests retrieved successfully")
    @Auditable(action = "READ", resourceType = "CONTEST")
    @GetMapping("/completed")
    public ResponseEntity<ApiResponseWrapper<List<ContestResponse>>> getCompletedContests() {
        List<ContestResponse> contests = contestService.getCompletedContests();
        return ApiResponseWrapper.success(contests, "Completed contests retrieved successfully");
    }

    @Operation(summary = "Get my joined contests", description = "Retrieves contests the current user has joined")
    @ApiResponse(responseCode = "200", description = "Joined contests retrieved successfully")
    @Auditable(action = "READ", resourceType = "CONTEST")
    @GetMapping("/my")
    public ResponseEntity<ApiResponseWrapper<List<ContestResponse>>> getMyContests() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<ContestResponse> contests = contestService.getJoinedContests(currentUser.getId());
        return ApiResponseWrapper.success(contests, "Joined contests retrieved successfully");
    }

    @Operation(summary = "Get contest detail", description = "Retrieves a single contest with the current user's participation status")
    @ApiResponse(responseCode = "200", description = "Contest retrieved successfully")
    @Auditable(action = "READ", resourceType = "CONTEST")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseWrapper<ContestResponse>> getContestDetail(
            @PathVariable @Parameter(description = "ID of the contest") Long id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ContestResponse response = contestService.getContestDetail(id, currentUser.getId());
        return ApiResponseWrapper.success(response, "Contest retrieved successfully");
    }

    @Operation(summary = "Update a contest", description = "Updates an existing contest's configuration by ID")
    @ApiResponse(responseCode = "200", description = "Contest updated successfully")
    @Auditable(action = "UPDATE", resourceType = "CONTEST")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('contest:update')")
    public ResponseEntity<ApiResponseWrapper<ContestResponse>> updateContest(
            @PathVariable @Parameter(description = "ID of the contest to update") Long id,
            @Valid @RequestBody ContestRequest req) {
        ContestResponse response = contestService.updateContest(id, req);
        return ApiResponseWrapper.success(response, "Contest updated successfully");
    }

    @Operation(summary = "Join a contest", description = "Allows the authenticated user to join a contest by ID")
    @ApiResponse(responseCode = "200", description = "Contest joined successfully")
    @Auditable(action = "JOIN", resourceType = "CONTEST")
    @PostMapping("/{contestId}/join")
    @PreAuthorize("hasAuthority('contest:join')")
    public ResponseEntity<ApiResponseWrapper<String>> joinContest(
            @PathVariable @Parameter(description = "ID of the contest to join") Long contestId) {
        String result = contestService.joinContest(contestId);
        return ApiResponseWrapper.success(result, result);
    }

    @Operation(summary = "Get contest participants", description = "Retrieves the list of participants for a specific contest")
    @ApiResponse(responseCode = "200", description = "Participants retrieved successfully")
    @Auditable(action = "READ", resourceType = "CONTEST")
    @GetMapping("/{contestId}/participants")
    public ResponseEntity<ApiResponseWrapper<List<ContestParticipant>>> getParticipants(
            @PathVariable @Parameter(description = "ID of the contest") Long contestId) {
        List<ContestParticipant> participants = contestService.getParticipants(contestId);
        return ApiResponseWrapper.success(participants, "Participants retrieved successfully");
    }

    @Operation(summary = "Cancel a contest", description = "Cancels an existing contest by ID")
    @ApiResponse(responseCode = "200", description = "Contest cancelled successfully")
    @Auditable(action = "DELETE", resourceType = "CONTEST")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('contest:delete')")
    public ResponseEntity<ApiResponseWrapper<Void>> cancelContest(
            @PathVariable @Parameter(description = "ID of the contest to cancel") Long id) {
        contestService.cancelContest(id);
        return ApiResponseWrapper.success(null, "Contest cancelled successfully");
    }
}
