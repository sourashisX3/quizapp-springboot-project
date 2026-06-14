package com.sourashis.quizapp.modules.contest.service;

import com.sourashis.quizapp.modules.auth.entity.User;
import com.sourashis.quizapp.modules.contest.dto.ContestRequest;
import com.sourashis.quizapp.modules.contest.dto.ContestResponse;
import com.sourashis.quizapp.modules.contest.entity.Contest;
import com.sourashis.quizapp.modules.contest.entity.ContestParticipant;
import com.sourashis.quizapp.modules.contest.repository.ContestParticipantRepository;
import com.sourashis.quizapp.modules.contest.repository.ContestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ContestService {

    @Autowired
    private ContestRepository contestRepository;

    @Autowired
    private ContestParticipantRepository contestParticipantRepository;

    public ContestResponse createContest(ContestRequest req) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Contest contest = Contest.builder()
                .uuid(UUID.randomUUID().toString())
                .title(req.getTitle())
                .description(req.getDescription())
                .contestType(req.getContestType())
                .categoryId(req.getCategoryId())
                .difficulty(req.getDifficulty())
                .numQuestions(req.getNumQuestions())
                .timeLimitMinutes(req.getTimeLimitMinutes())
                .startsAt(req.getStartsAt())
                .endsAt(req.getEndsAt())
                .maxParticipants(req.getMaxParticipants())
                .minScoreToQualify(req.getMinScoreToQualify())
                .rulesJson(req.getRulesJson())
                .prizeDescription(req.getPrizeDescription())
                .createdBy(currentUser.getId())
                .build();

        contest = contestRepository.save(contest);
        return toResponse(contest);
    }

    @Transactional(readOnly = true)
    public List<ContestResponse> getActiveContests() {
        Instant now = Instant.now();
        return contestRepository.findByIsActiveTrueAndStartsAtBeforeAndEndsAtAfter(now, now)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ContestResponse> getUpcomingContests() {
        Instant now = Instant.now();
        return contestRepository.findByIsActiveTrueAndStartsAtBeforeAndEndsAtAfter(now, now)
                .stream().map(this::toResponse).toList();
    }

    public String joinContest(Long contestId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Contest contest = contestRepository.findById(contestId).orElseThrow();

        if (contestParticipantRepository.findByContestIdAndUserId(contestId, currentUser.getId()).isPresent()) {
            return "Already registered";
        }

        ContestParticipant participant = ContestParticipant.builder()
                .contestId(contestId)
                .userId(currentUser.getId())
                .status("REGISTERED")
                .build();

        contestParticipantRepository.save(participant);
        return "Registered successfully";
    }

    public List<ContestParticipant> getParticipants(Long contestId) {
        return contestParticipantRepository.findByContestIdOrderByScoreDesc(contestId);
    }

    private ContestResponse toResponse(Contest c) {
        return ContestResponse.builder()
                .id(c.getId())
                .uuid(c.getUuid())
                .title(c.getTitle())
                .description(c.getDescription())
                .contestType(c.getContestType())
                .categoryId(c.getCategoryId())
                .difficulty(c.getDifficulty())
                .numQuestions(c.getNumQuestions())
                .timeLimitMinutes(c.getTimeLimitMinutes())
                .startsAt(c.getStartsAt())
                .endsAt(c.getEndsAt())
                .maxParticipants(c.getMaxParticipants())
                .minScoreToQualify(c.getMinScoreToQualify())
                .isActive(c.getIsActive())
                .rulesJson(c.getRulesJson())
                .prizeDescription(c.getPrizeDescription())
                .createdBy(c.getCreatedBy())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
