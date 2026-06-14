package com.sourashis.quizapp.modules.quiz.repository;

import com.sourashis.quizapp.modules.quiz.entity.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    Optional<Quiz> findByUuid(String uuid);

    Page<Quiz> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Quiz> findByIsPublishedTrue(Pageable pageable);

    List<Quiz> findByCreatedById(Long createdById);
}
