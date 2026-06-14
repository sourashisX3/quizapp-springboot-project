package com.sourashis.quizapp.modules.quiz.repository;

import com.sourashis.quizapp.modules.quiz.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    Optional<QuizAttempt> findByUuid(String uuid);

    List<QuizAttempt> findByUserId(Long userId);

    List<QuizAttempt> findByUserIdAndQuizId(Long userId, Long quizId);

    List<QuizAttempt> findByQuizId(Long quizId);

    List<QuizAttempt> findByContestParticipantId(Long contestParticipantId);

    Optional<QuizAttempt> findTopByUserIdAndQuizIdOrderByScoreDesc(Long userId, Long quizId);

    long countByUserIdAndQuizId(Long userId, Long quizId);
}
