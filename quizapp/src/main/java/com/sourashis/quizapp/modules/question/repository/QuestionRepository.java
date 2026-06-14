package com.sourashis.quizapp.modules.question.repository;

import com.sourashis.quizapp.modules.question.entity.DifficultyLevel;
import com.sourashis.quizapp.modules.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByCategoryId(Long categoryId);

    Page<Question> findByCategoryId(Long categoryId, Pageable pageable);

    @Query(value = "SELECT * FROM questions q WHERE q.category_id = ?1 AND q.is_active = true ORDER BY RAND() LIMIT ?2", nativeQuery = true)
    List<Question> findRandomQuestionsByCategory(@Param("categoryId") Long categoryId, @Param("limit") int limit);

    Page<Question> findByIsActiveTrue(Pageable pageable);

    List<Question> findByCategoryIdAndDifficulty(Long categoryId, DifficultyLevel difficulty);
}
