package com.sourashis.quizapp.modules.contest.repository;

import com.sourashis.quizapp.modules.contest.entity.ContestParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContestParticipantRepository extends JpaRepository<ContestParticipant, Long> {

    List<ContestParticipant> findByContestId(Long contestId);

    Optional<ContestParticipant> findByContestIdAndUserId(Long contestId, Long userId);

    List<ContestParticipant> findByContestIdOrderByScoreDesc(Long contestId);

    long countByContestId(Long contestId);

    List<ContestParticipant> findByUserId(Long userId);
}
