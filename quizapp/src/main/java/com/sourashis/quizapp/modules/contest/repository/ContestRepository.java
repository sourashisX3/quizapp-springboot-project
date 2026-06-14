package com.sourashis.quizapp.modules.contest.repository;

import com.sourashis.quizapp.modules.contest.entity.Contest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContestRepository extends JpaRepository<Contest, Long> {

    Optional<Contest> findByUuid(String uuid);

    List<Contest> findByContestTypeAndIsActiveTrue(String contestType);

    List<Contest> findByIsActiveTrueAndStartsAtBeforeAndEndsAtAfter(Instant start, Instant end);

    List<Contest> findByEndsAtBeforeAndIsActiveTrue(Instant endsAt);
}
