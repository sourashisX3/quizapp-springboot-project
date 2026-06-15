package com.sourashis.quizapp.modules.contest.service;

import com.sourashis.quizapp.modules.auth.entity.User;
import com.sourashis.quizapp.modules.auth.repository.UserRepository;
import com.sourashis.quizapp.modules.contest.dto.ContestRequest;
import com.sourashis.quizapp.modules.contest.dto.ContestResponse;
import com.sourashis.quizapp.modules.contest.entity.Contest;
import com.sourashis.quizapp.modules.contest.entity.ContestParticipant;
import com.sourashis.quizapp.modules.contest.exception.ContestNotFoundException;
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

    @Autowired
    private UserRepository userRepository;

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
    public ContestResponse getContest(Long id) {
        Contest contest = contestRepository.findById(id)
                .orElseThrow(() -> new ContestNotFoundException(id));
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
        return contestRepository.findByIsActiveTrueAndStartsAtAfter(now)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ContestResponse> getCompletedContests() {
        Instant now = Instant.now();
        return contestRepository.findByEndsAtBeforeAndIsActiveTrue(now)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ContestResponse> getAllContests() {
        return contestRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ContestResponse> getJoinedContests(Long userId) {
        List<ContestParticipant> participants = contestParticipantRepository.findByUserId(userId);
        return participants.stream()
                .map(p -> contestRepository.findById(p.getContestId()).orElse(null))
                .filter(c -> c != null)
                .map(this::toResponse)
                .toList();
    }

    public ContestResponse updateContest(Long id, ContestRequest req) {
        Contest contest = contestRepository.findById(id)
                .orElseThrow(() -> new ContestNotFoundException(id));

        contest.setTitle(req.getTitle());
        contest.setDescription(req.getDescription());
        contest.setContestType(req.getContestType());
        contest.setCategoryId(req.getCategoryId());
        contest.setDifficulty(req.getDifficulty());
        contest.setNumQuestions(req.getNumQuestions());
        contest.setTimeLimitMinutes(req.getTimeLimitMinutes());
        contest.setStartsAt(req.getStartsAt());
        contest.setEndsAt(req.getEndsAt());
        contest.setMaxParticipants(req.getMaxParticipants());
        contest.setMinScoreToQualify(req.getMinScoreToQualify());
        contest.setRulesJson(req.getRulesJson());
        contest.setPrizeDescription(req.getPrizeDescription());

        contest = contestRepository.save(contest);
        return toResponse(contest);
    }

    public void cancelContest(Long id) {
        Contest contest = contestRepository.findById(id)
                .orElseThrow(() -> new ContestNotFoundException(id));
        contest.setIsActive(false);
        contestRepository.save(contest);
    }

    public String joinContest(Long contestId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ContestNotFoundException(contestId));

        if (contestParticipantRepository.findByContestIdAndUserId(contestId, currentUser.getId()).isPresent()) {
            return "Already registered";
        }

        if (!contest.getIsActive()) {
            throw new IllegalStateException("Contest is no longer active");
        }

        if (contest.getStartsAt().isBefore(Instant.now())) {
            throw new IllegalStateException("Contest has already started");
        }

        if (contest.getMaxParticipants() > 0) {
            long currentCount = contestParticipantRepository.countByContestId(contestId);
            if (currentCount >= contest.getMaxParticipants()) {
                throw new IllegalStateException("Contest is full");
            }
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

    public void updateParticipantScore(Long contestId, Long userId, int score, int timeTakenSeconds) {
        ContestParticipant participant = contestParticipantRepository
                .findByContestIdAndUserId(contestId, userId)
                .orElseThrow(() -> new RuntimeException("Participant not found for contest " + contestId + " and user " + userId));

        participant.setScore(score);
        participant.setTimeTakenSeconds(timeTakenSeconds);
        participant.setStatus("COMPLETED");
        participant.setCompletedAt(Instant.now());
        contestParticipantRepository.save(participant);

        recalculateRanks(contestId);
    }

    private void recalculateRanks(Long contestId) {
        List<ContestParticipant> participants = contestParticipantRepository.findByContestIdOrderByScoreDesc(contestId);
        int rank = 1;
        for (ContestParticipant p : participants) {
            p.setPositionRank(rank++);
        }
        contestParticipantRepository.saveAll(participants);
    }

    @Transactional(readOnly = true)
    public ContestResponse getContestDetail(Long contestId, Long userId) {
        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() -> new ContestNotFoundException(contestId));

        ContestResponse response = toResponse(contest);

        contestParticipantRepository.findByContestIdAndUserId(contestId, userId).ifPresent(participant -> {
            response.setUserStatus(participant.getStatus());
            response.setUserScore(participant.getScore());
            response.setUserRank(participant.getPositionRank());
        });

        return response;
    }

    private ContestResponse toResponse(Contest c) {
        String createdByUsername = null;
        if (c.getCreatedBy() != null) {
            createdByUsername = userRepository.findById(c.getCreatedBy())
                    .map(User::getUsername).orElse(null);
        }

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
                .createdByUsername(createdByUsername)
                .build();
    }
}
